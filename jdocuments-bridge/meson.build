project(
  'bridge',
  'c',
  version : '1.0.0',
  meson_version : '>= 1.3.0',
  default_options : ['warning_level=3'],
)

# These arguments are only used to build the shared library
# not the executables that use the library.
lib_args = ['-DBUILDING_BRIDGE']

dependencies = [
]

lib = library(
  'bridge',
  'bridge.c',
  install : true,
  c_shared_args : lib_args,
  gnu_symbol_visibility : 'hidden',
  dependencies : dependencies,
)

test_exe = executable(
  'bridge_test',
  'test/bridge_test.c',
  dependencies : dependencies,
  link_with : lib,
)
test('bridge', test_exe)

# Make this library usable as a Meson subproject.
bridge_dep = declare_dependency(
  include_directories : include_directories('include'),
  dependencies : dependencies,
  link_with : lib,
)
meson.override_dependency('bridge', bridge_dep)

# Make this library usable from the system's
# package manager.
install_headers('include/bridge.h', subdir : 'bridge')

pkg_mod = import('pkgconfig')
pkg_mod.generate(
  lib,
  description : 'Meson sample project.',
  subdirs : 'bridge',
)
