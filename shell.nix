{ pkgs ? import <nixpkgs> { } }:

pkgs.mkShell {
  packages = [ pkgs.jdk25 ];

  JAVA_HOME = pkgs.jdk25.home;

  # NanoVG/GLFW natives need GL and X11/Wayland libraries at runtime.
  LD_LIBRARY_PATH = pkgs.lib.makeLibraryPath [
    pkgs.libGL
    pkgs.glfw
    pkgs.libx11
    pkgs.wayland
  ];
}
