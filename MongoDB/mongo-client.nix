# nix-shell ./mongo-client.nix --run bash
{ nixpkgs ? import <nixpkgs> {} }:

with nixpkgs;

let jdk = pkgs.jdk17;

in
pkgs.stdenv.mkDerivation rec {
  name = "nix-mongo-shell";
  buildInputs = [
    mongosh
  ];
}
