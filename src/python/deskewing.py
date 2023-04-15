import napari
import numpy as np
import tifffile
import time

def compute_stage_coords_um(image_z_um, image_x_um_relative, theta):
    # offset of image x coordinate to convert relative to absolute
    delta = np.tan(theta) * image_z_um
    image_x_um_absolute = image_x_um_relative + delta
    stage_x_um = image_x_um_absolute * np.sin(theta)
    stage_z_um = image_z_um * np.cos(theta)
    return stage_x_um, stage_z_um

def compute_remapped_coordinate_space(camera_pixel_size_xy_um, z_step_um,  z_pixel_shape, x_pixel_shape, y_pixel_shape,
                                      THETA):
    # compute the coordinates in um of pixels in the image
    image_x_coords_um = np.arange(x_pixel_shape) * camera_pixel_size_xy_um
    image_z_coords_um = np.arange(z_pixel_shape) * z_step_um
    # compute the um extent of "stage" coordinates, which the images will be remapped to
    min_stage_coord_x_um, min_stage_coord_z_um = compute_stage_coords_um(image_z_coords_um[0], image_x_coords_um[0],
                                                                         THETA)
    max_stage_coord_x_um, max_stage_coord_z_um = compute_stage_coords_um(image_z_coords_um[-1], image_x_coords_um[-1],
                                                                         THETA)
    total_stage_extent_x_um = max_stage_coord_x_um - min_stage_coord_x_um
    total_stage_extent_z_um = max_stage_coord_z_um - min_stage_coord_z_um

    # Figure out the shape of the remapped image
    # The y pixel size is fixed by the camera/optics. anchor other pixels sizes to this for isotropic pixels
    reconstruction_voxel_size_um = camera_pixel_size_xy_um
    remapped_image_x_shape = int(total_stage_extent_x_um / reconstruction_voxel_size_um)
    remapped_image_y_shape = y_pixel_shape
    remapped_image_z_shape = int(total_stage_extent_z_um / reconstruction_voxel_size_um)

    return image_z_coords_um, image_x_coords_um, remapped_image_z_shape, remapped_image_x_shape, remapped_image_y_shape, reconstruction_voxel_size_um

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


def precompute_coord_transform_LUTs(z_pixel_shape, x_pixel_shape, remapped_image_z_shape, remapped_image_x_shape,
                                    image_z_coords_um, image_x_coords_um, camera_pixel_size_xy_um, THETA):
    # precompute a look up table of stage coordinates to remapped image coordinates
    dest_x_coord_LUT = np.zeros((z_pixel_shape, x_pixel_shape), dtype=int)
    dest_z_coord_LUT = np.zeros((z_pixel_shape, x_pixel_shape), dtype=int)
    for z_index_camera in range(z_pixel_shape):
        # get the image on the camera at this z slice
        image_z_um = image_z_coords_um[z_index_camera]
        for x_index_camera in range(x_pixel_shape):
            image_x_um = image_x_coords_um[x_index_camera]
            # compute the exact um coords in the new space that these pixels should be mapped to
            stage_x_um, stage_z_um = compute_stage_coords_um(image_z_um, image_x_um, THETA)
            # assign these pixels to integer coordinates using nearest neighbors interpolation
            # grab the line of pixels along the y axis at this x and z position
            # compute destination x coordinate in the stage space using nearest neighbors interpolation and
            # make sure rounding doesnt exceed valid coords
            stage_x_index = min(int(np.round(stage_x_um / camera_pixel_size_xy_um)), remapped_image_x_shape - 1)
            stage_z_index = min(int(np.round(stage_z_um / camera_pixel_size_xy_um)), remapped_image_z_shape - 1)

            dest_x_coord_LUT[z_index_camera, x_index_camera] = stage_x_index
            dest_z_coord_LUT[z_index_camera, x_index_camera] = stage_z_index
    return dest_x_coord_LUT, dest_z_coord_LUT



# THETA = np.pi / 4
THETA = (np.pi / 4) * 1.1

data, z_step_um, camera_pixel_size_xy_um = load_demo_data()
z_pixel_shape, x_pixel_shape, y_pixel_shape = data.shape

image_z_coords_um, image_x_coords_um, remapped_image_z_shape, remapped_image_x_shape, remapped_image_y_shape, reconstruction_voxel_size_um = \
    compute_remapped_coordinate_space( camera_pixel_size_xy_um, z_step_um,  z_pixel_shape, x_pixel_shape, y_pixel_shape, THETA)

start = time.time()
dest_x_coord_LUT, dest_z_coord_LUT = precompute_coord_transform_LUTs(z_pixel_shape, x_pixel_shape, remapped_image_z_shape,
                                             remapped_image_x_shape, image_z_coords_um, image_x_coords_um, camera_pixel_size_xy_um, THETA)
print('compute LUT: {} s '.format(time.time() - start))


sum_projection_z = np.zeros((remapped_image_x_shape, remapped_image_y_shape), dtype=float)
sum_projection_x = np.zeros((remapped_image_z_shape, remapped_image_y_shape), dtype=float)
recon_volume = np.zeros((remapped_image_z_shape, remapped_image_x_shape, remapped_image_y_shape), dtype=float)

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
        stage_x_index = dest_x_coord_LUT[z_index_camera, x_index_camera]
        stage_z_index = dest_z_coord_LUT[z_index_camera, x_index_camera]

        source_line_of_pixels = image_on_camera[x_index_camera]
        sum_projection_z[stage_x_index, :] += source_line_of_pixels
        sum_projection_x[stage_z_index, :] += source_line_of_pixels
        recon_volume[stage_z_index, stage_x_index, :] += source_line_of_pixels
        pixel_count_sum_projection_z[stage_x_index, :] += 1
        pixel_count_sum_projection_x[stage_z_index, :] += 1
        pixel_count_recon_volume[stage_z_index, stage_x_index, :] += 1

# divide by the number of pixels that were summed to get the average
mean_projection_z = sum_projection_z / pixel_count_sum_projection_z
mean_projection_x = sum_projection_x / pixel_count_sum_projection_x
mean_recon_volume = recon_volume / pixel_count_recon_volume

print('projection time', time.time() - start)

import napari
viewer = napari.Viewer()
viewer.add_image(mean_projection_z, name='mean_projection_z')
viewer.add_image(mean_projection_x, name='mean_projection_x')
viewer.add_image(mean_recon_volume, name='mean_recon_volume')
pass