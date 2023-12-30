{ nixpkgs ? import <nixpkgs> {
   config.allowUnfree = true;
} }:

with nixpkgs;


pkgs.stdenv.mkDerivation rec {
  name = "nix-mongo-shell";
  buildInputs = [
    mongosh
    mongodb-compass
  ];
}
