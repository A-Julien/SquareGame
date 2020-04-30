#  DevOps


[![Build Status](https://travis-ci.com/A-Julien/devops.svg?branch=master)](https://travis-ci.com/A-Julien/tacOS) 
[![codecov](https://codecov.io/gh/A-Julien/devops/branch/master/graph/badge.svg)](https://codecov.io/gh/A-Julien/devops)
[![codeinsp](https://www.code-inspector.com/project/5990/score/svg)](https://frontend.code-inspector.com/public/project/5990/devops/dashboard)
[![CodeFactor](https://www.codefactor.io/repository/github/a-julien/devops/badge)](https://www.codefactor.io/repository/github/a-julien/devops)

## Project architecture:

*	Github : https://github.com/A-Julien/SquareGame
*	CI (Travis) : https://travis-ci.com/A-Julien/SquareGame
*	MavenDoc : [mavendoc](https://a-julien.github.io/SquareGame)
*   JavaDoc : [javadoc](https://a-julien.github.io/SquareGame/apidocs/index.html)

## Fonctionnalités implémentées

* Créer des dataframes à partir de tableaux d'objets.
* Créer des dataframes à partir de fichiers CSV.
* Sélectionner les colonnes d'un dataframe.
* Sélectionner les lignes d'un dataframe.
* Affichage entier d'un dataframe.
* Affichage partiel (```head() & tail()```) .
* Statistiques sur un dataframe (moyenne, minimum, maximum).

## Déploiement :

Une image docker est disponible sur  
[dockerHub](https://hub.docker.com/repository/docker/jalaimo/travis-ci-build-devops).

Cette image a pour but de fournir une démonstration de la librairie. 
Elle fournit aussi un environnement de développement pour les développers.
Elle est basée sur une image de linux alpine.  

## Documentation

La documentation des fonctions est disponnible dans la [javadoc](https://a-julien.github.io/devops/apidocs/index.html)

## Outils utilisés

* Maven (tests, génération de la javadoc et d'un jar)
* Travis pour la CI (tests, déploiment de la doc et de docker)
* CodeCov pour la couverture des test (avec jacoco)
* CodeInspector pour la qualité du code
* Terraform & Ansible pour déployer l'application sur VM
