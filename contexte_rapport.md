# Contexte et Implémentation du Mini-Projet : Multi-Agent Systems & Planning

**Université :** USTHB, Faculty of Computer Science  
**Niveau :** M1 SII - S2  
**Module :** Agents Technology (Prof. B. Dellal-Hedjazi)  
**Année :** 2025-2026  

---

## Introduction Générale
Ce document contient toutes les informations techniques et l'architecture du code implémenté pour le mini-projet. L'objectif est de fournir suffisamment de contexte pour rédiger un rapport académique complet. Le projet est divisé en 4 parties indépendantes abordant différents concepts des systèmes multi-agents (JADE) et de la planification (Python/AIMA).

---

## Partie 1 : Négociation Multi-Agents (Enchères)
**Objectif :** Créer un système d'enchères avec 1 vendeur et plusieurs acheteurs.
- **Technologies :** Java, framework JADE, Interface graphique Java Swing.
- **Fonctionnement :**
  - Une interface (`auc.java` réécrite en pur Java sans dépendances IDE) permet de configurer l'enchère : nombre d'acheteurs, durée de l'enchère, prix de départ, et prix de réserve.
  - **L'Agent Vendeur (`seller.java`) :** Initialise l'enchère, envoie le prix aux acheteurs, attend les propositions. Si le temps expire, il vérifie si le dernier prix dépasse le prix de réserve. Si oui, il déclare un gagnant, sinon l'objet n'est pas vendu.
  - **Les Agents Acheteurs (`buyers.java`) :** Reçoivent le prix, et font des propositions aléatoires en utilisant un comportement cyclique (`CyclicBehaviour`).
  - La communication est gérée via le `TopicManagementHelper` de JADE pour le multicast (topics "add", "winner", "no winner", "end", "stop", "buy").

---

## Partie 2 : Décision Multicritère et Agents Mobiles
**Objectif :** Implémenter un acheteur mobile qui visite plusieurs vendeurs et prend une décision multicritère.
- **Technologies :** Java, framework JADE.
- **Architecture :**
  - Un conteneur principal (`Main-Container`) et plusieurs conteneurs périphériques (`Container-1`, `Container-2`) hébergeant chacun un agent vendeur (`SellerAgent`).
  - **Agent Acheteur (`MobileBuyerAgent`) :** Un agent mobile qui utilise la fonctionnalité de migration de JADE (`doMove`). Il se déplace physiquement vers `Container-1`, demande une offre, puis se déplace vers `Container-2` pour une autre offre.
  - **Décision Multicritère :** Une fois toutes les offres collectées, il revient au conteneur principal et calcule un score pour chaque offre en combinant : Prix (minimiser), Délai de livraison (minimiser) et Qualité (maximiser).
  - La fonction d'utilité est : `Score = (Quality * 10) - (Price * 0.5) - (DeliveryTime * 2)`.

---

## Partie 3 : Planification AIMA (Code Python)
**Objectif :** Tester le code de planification du dépôt AIMA (Artificial Intelligence: A Modern Approach).
- **Technologies :** Python 3, `aima-python`, `networkx`, `matplotlib`, `tkinter`.
- **Implémentation (`test_aima_planning.py`) :**
  - Utilisation du planificateur à ordre partiel (`PartialOrderPlanner` ou POP).
  - Modélisation du problème classique "Chaussures et Chaussettes" (Shoes & Socks).
  - **État initial :** `RightSockOff`, `LeftSockOff`, `RightShoeOff`, `LeftShoeOff`.
  - **But :** `RightShoeOn`, `LeftShoeOn`.
  - **Actions :** Mettre la chaussette droite/gauche, Mettre la chaussure droite/gauche.
  - **Valeur Ajoutée (Interface) :** Au lieu d'afficher le résultat en texte brut dans la console, le code intercepte la sortie du planificateur et utilise `networkx` et `matplotlib` pour dessiner un graphe orienté affichant visuellement les contraintes temporelles (ex: "Mettre Chaussette" avant "Mettre Chaussure") et les liens causaux.

---

## Partie 4 : Planification Multi-Agents (Centralisée)
**Objectif :** Implémenter un exemple de planification multi-agents avec un planificateur centralisé qui distribue des plans.
- **Technologies :** Java, framework JADE.
- **Architecture :**
  - **Le Planificateur Central (`CentralPlannerAgent`) :** Il agit comme le cerveau. Il attend que les robots s'enregistrent chez lui. Une fois enregistrés, il génère un plan global, le décompose en sous-tâches, et distribue les actions spécifiques à chaque robot via des messages ACL (`ACLMessage.REQUEST`).
  - **Les Robots (`RobotAgent`) :** Ils s'inscrivent auprès du planificateur central, attendent de recevoir leur plan de tâches, et les exécutent séquentiellement (simulé par un délai et un affichage console), avant d'informer le planificateur de la fin de leur tâche.
  - Cette partie illustre la coordination et la délégation des tâches dans un MAS distribué.

---

## Instructions pour l'IA rédactrice (Claude)
À partir de ces éléments techniques, veuillez générer un rapport de projet structuré contenant :
1. Une introduction au projet et aux concepts (JADE, MAS, Planification).
2. Une description détaillée du design et du choix d'implémentation pour chacune des 4 parties.
3. Une conclusion sur l'intérêt des systèmes multi-agents (négociation, mobilité, intelligence).
4. Un ton académique adapté à un rendu universitaire de niveau Master.
