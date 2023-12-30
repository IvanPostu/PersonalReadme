{ nixpkgs ? import <nixpkgs> {
   config.allowUnfree = true;
} }:

with nixpkgs;

pkgs.mkShell {
  name = "nix-mongo-shell";
  buildInputs = [ 
    mongosh
    mongodb-compass
    jdk17
  ];
  shellHook = ''
    export JAVA_HOME=${pkgs.jdk17}
    export PATH=$JAVA_HOME/bin:$PATH
  '';
  pure = true;
}
