import napari
import numpy as np
import tifffile
import time

class ObliqueStackProcessor:

    def __init__(self, theta, camera_pixel_size_xy_um, z_step_um, z_pixel_shape, x_pixel_shape, y_pixel_shape):
        self.theta = theta
        self.z_pixel_shape = z_pixel_shape
        self.x_pixel_shape = x_pixel_shape
        self.y_pixel_shape = y_pixel_shape
        self.camera_pixel_size_xy_um = camera_pixel_size_xy_um
        self.z_step_um = z_step_um
        self.compute_remapped_coordinate_space()
        self.precompute_coord_transform_LUTs()


    def compute_stage_coords_um(self, image_z_um, image_x_um_relative):
        # offset of image x coordinate to convert relative to absolute
        delta = np.tan(self.theta) * image_z_um
        image_x_um_absolute = image_x_um_relative + delta
        stage_x_um = image_x_um_absolute * np.sin(self.theta)
        stage_z_um = image_z_um * np.cos(self.theta)
        return stage_x_um, stage_z_um

    def compute_remapped_coordinate_space(self):
        # compute the coordinates in um of pixels in the image
        self.image_x_coords_um = np.arange(self.x_pixel_shape) * self.camera_pixel_size_xy_um
        self.image_z_coords_um = np.arange(self.z_pixel_shape) * self.z_step_um
        # compute the um extent of "stage" coordinates, which the images will be remapped to
        min_stage_coord_x_um, min_stage_coord_z_um = self.compute_stage_coords_um(
            self.image_z_coords_um[0], self.image_x_coords_um[0])
        max_stage_coord_x_um, max_stage_coord_z_um = self.compute_stage_coords_um(
            self.image_z_coords_um[-1], self.image_x_coords_um[-1])
        total_stage_extent_x_um = max_stage_coord_x_um - min_stage_coord_x_um
        total_stage_extent_z_um = max_stage_coord_z_um - min_stage_coord_z_um

        # Figure out the shape of the remapped image
        # The y pixel size is fixed by the camera/optics. anchor other pixels sizes to this for isotropic pixels
        self.reconstruction_voxel_size_um = camera_pixel_size_xy_um
        self.remapped_image_x_shape = int(total_stage_extent_x_um / self.reconstruction_voxel_size_um)
        self.remapped_image_y_shape = y_pixel_shape
        self.remapped_image_z_shape = int(total_stage_extent_z_um / self.reconstruction_voxel_size_um)

    def precompute_coord_transform_LUTs(self):
        # precompute a look up table of stage coordinates to remapped image coordinates
        self.dest_x_coord_LUT = np.zeros((self.z_pixel_shape, self.x_pixel_shape), dtype=int)
        self.dest_z_coord_LUT = np.zeros((self.z_pixel_shape, self.x_pixel_shape), dtype=int)
        for z_index_camera in range(self.z_pixel_shape):
            # get the image on the camera at this z slice
            image_z_um = self.image_z_coords_um[z_index_camera]
            for x_index_camera in range(self.x_pixel_shape):
                image_x_um = self.image_x_coords_um[x_index_camera]
                # compute the exact um coords in the new space that these pixels should be mapped to
                stage_x_um, stage_z_um = self.compute_stage_coords_um(image_z_um, image_x_um)
                # assign these pixels to integer coordinates using nearest neighbors interpolation
                # grab the line of pixels along the y axis at this x and z position
                # compute destination x coordinate in the stage space using nearest neighbors interpolation and
                # make sure rounding doesnt exceed valid coords
                stage_x_index = min(int(np.round(stage_x_um / self.camera_pixel_size_xy_um)), self.remapped_image_x_shape - 1)
                stage_z_index = min(int(np.round(stage_z_um / self.camera_pixel_size_xy_um)), self.remapped_image_z_shape - 1)

                self.dest_x_coord_LUT[z_index_camera, x_index_camera] = stage_x_index
                self.dest_z_coord_LUT[z_index_camera, x_index_camera] = stage_z_index


    def make_projections(self, data):
        sum_projection_z = np.zeros((self.remapped_image_x_shape, self.remapped_image_y_shape), dtype=float)
        sum_projection_x = np.zeros((self.remapped_image_z_shape, self.remapped_image_y_shape), dtype=float)
        recon_volume = np.zeros((self.remapped_image_z_shape, self.remapped_image_x_shape, self.remapped_image_y_shape), dtype=float)

        pixel_count_sum_projection_z = np.zeros_like(sum_projection_z).astype(int)
        pixel_count_sum_projection_x = np.zeros_like(sum_projection_x).astype(int)
        pixel_count_recon_volume = np.zeros_like(recon_volume).astype(int)

        # do the projection
        # iterate through each z slice of the image
        # at each z slice, iterate through each x pixel and copy a line of y pixels to the new image
        start = time.time()
        for z_index_camera in np.arange(0, z_pixel_shape, 1):
            image_on_camera = data[z_index_camera]
            for x_index_camera in range(x_pixel_shape):
                # where does each line of y pixels belong in the new image?
                stage_x_index = self.dest_x_coord_LUT[z_index_camera, x_index_camera]
                stage_z_index = self.dest_z_coord_LUT[z_index_camera, x_index_camera]

                source_line_of_pixels = image_on_camera[x_index_camera]
                sum_projection_z[stage_x_index, :] += source_line_of_pixels
                sum_projection_x[stage_z_index, :] += source_line_of_pixels
                recon_volume[stage_z_index, stage_x_index, :] += source_line_of_pixels
                pixel_count_sum_projection_z[stage_x_index, :] += 1
                pixel_count_sum_projection_x[stage_z_index, :] += 1
                pixel_count_recon_volume[stage_z_index, stage_x_index, :] += 1

        # avoid division by 0
        pixel_count_sum_projection_z[pixel_count_sum_projection_z == 0] = 1
        pixel_count_sum_projection_x[pixel_count_sum_projection_x == 0] = 1
        pixel_count_recon_volume[pixel_count_recon_volume == 0] = 1

        # divide by the number of pixels that were summed to get the average
        mean_projection_z = sum_projection_z / pixel_count_sum_projection_z
        mean_projection_x = sum_projection_x / pixel_count_sum_projection_x
        mean_recon_volume = recon_volume / pixel_count_recon_volume
        return mean_projection_z, mean_projection_x, mean_recon_volume

def load_demo_data():
    # load a tiff stack
    tiff_path = r'C:\Users\henry\Desktop\demo_snouty.tif'
    z_step_um = 0.13
    pixel_size_xy_um = 0.116

    # Read the TIFF stack into a NumPy array
    with tifffile.TiffFile(tiff_path) as tif:
        data = tif.asarray()
    # reverse the z axis so that the first image is at the top of the stack
    data = data[::-1]
    # z x y order
    return data, z_step_um, pixel_size_xy_um



# THETA = np.pi / 4
THETA = (np.pi / 4) * 1.05

data, z_step_um, camera_pixel_size_xy_um = load_demo_data()
z_pixel_shape, x_pixel_shape, y_pixel_shape = data.shape

processor = ObliqueStackProcessor(THETA, camera_pixel_size_xy_um, z_step_um,
                                  z_pixel_shape, x_pixel_shape, y_pixel_shape)


mean_projection_z, mean_projection_x, mean_recon_volume = processor.make_projections(data)


import napari
viewer = napari.Viewer()
viewer.add_image(mean_projection_z, name='mean_projection_z')
viewer.add_image(mean_projection_x, name='mean_projection_x')
viewer.add_image(mean_recon_volume, name='mean_recon_volume')
viewer.add_image(data, name='raw data')


pass