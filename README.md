# Veuillez lire ce readme #

Ce readme docummente tout ce dont vous avez besoin de savoir

### À quoi sert ce repo ? ###

* Serveur de connexion pour serveur privé Dofus
* Version 1.0.5
* Par Jason Larochelle

###  Comment compiller ce projet ? ###

* Le projet est habituellement compilé à l'aide d'IntelliJ
* Le projet utilise plusieurs librairies: Apache Commons (Collections 3.2.2, Configurations 1.10, lang 2.6, logging 1.2), Jansi 1.7, Lombok, Apache Mina Core 2.0.16, Mysql Connector Java 5.1.40, Slf4 (Api 1.7.21 & Simple 1.7.22)
* Le projet dépends de toutes les librairies citées plus haut, ainsi qu'un plugin de support Lombok pour votre IDE, afin de gérer les annotations de @ Getter, @ Setter. Dépendant de l'IDE, l'annotion processing doit être activé dans le compilleur
* Les fichiers de configurations ne sont pas inclus dans ce repo, toutefois il peut être re-créer.
* Le format du fichier de configuration est en XML, utiliser la classe Config pour faire une représentation des noms des nodes et de leur valeurs possibles.
