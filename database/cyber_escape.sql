-- phpMyAdmin SQL Dump
-- version 5.2.3
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le : mer. 25 nov. 2026 à 16:05
-- Version du serveur : 8.4.7
-- Version de PHP : 8.3.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `cyber_escape`
--

-- --------------------------------------------------------

--
-- Structure de la table `answer_option`
--

DROP TABLE IF EXISTS `answer_option`;
CREATE TABLE IF NOT EXISTS `answer_option` (
  `id` int NOT NULL AUTO_INCREMENT,
  `question_id` int NOT NULL,
  `option_text` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_correct` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_option_question` (`question_id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `answer_option`
--

INSERT INTO `answer_option` (`id`, `question_id`, `option_text`, `is_correct`) VALUES
(1, 1, '123456', 0),
(2, 1, 'azerty', 0),
(3, 1, 'M@r!em_2026#Secure', 1),
(4, 1, 'password', 0),
(5, 2, 'Pour gagner du temps', 0),
(6, 2, 'Parce qu un pirate pourrait accéder à plusieurs comptes', 1),
(7, 2, 'Parce que les sites interdisent les mots de passe identiques', 0),
(8, 2, 'Cela ne présente aucun risque', 0),
(9, 3, 'Utiliser le même mot de passe partout', 0),
(10, 3, 'Écrire ses mots de passe sur les réseaux sociaux', 0),
(11, 3, 'Utiliser un gestionnaire de mots de passe', 1),
(12, 3, 'Choisir uniquement des chiffres', 0),
(13, 4, 'Cliquer rapidement sur le lien', 0),
(14, 4, 'Envoyer immédiatement ses coordonnées', 0),
(15, 4, 'Ignorer les signes suspects et répondre', 0),
(16, 4, 'Vérifier l expéditeur et contacter directement l organisme', 1),
(17, 5, 'Une adresse email inconnue ou suspecte', 1),
(18, 5, 'Un email provenant toujours de son professeur', 0),
(19, 5, 'Un message personnel attendu', 0),
(20, 5, 'Un email sans lien ni pièce jointe', 0),
(21, 6, 'Lui envoyer le mot de passe', 0),
(22, 6, 'Ne jamais communiquer son mot de passe', 1),
(23, 6, 'Lui envoyer uniquement son identifiant', 0),
(24, 6, 'Répondre avec son ancien mot de passe', 0),
(25, 7, 'Accélérer Internet', 0),
(26, 7, 'Augmenter la mémoire de l ordinateur', 0),
(27, 7, 'Contrôler les communications et bloquer les accès non autorisés', 1),
(28, 7, 'Supprimer automatiquement les virus', 0),
(29, 8, 'Un protocole de transfert de fichiers', 0),
(30, 8, 'Une connexion sécurisée grâce au chiffrement', 1),
(31, 8, 'Un logiciel antivirus', 0),
(32, 8, 'Un réseau social', 0),
(33, 9, 'Effectuer des opérations bancaires sans précaution', 0),
(34, 9, 'Partager ses mots de passe', 0),
(35, 9, 'Éviter les opérations sensibles sur un réseau public non sécurisé', 1),
(36, 9, 'Désactiver toutes les protections', 0);

-- --------------------------------------------------------

--
-- Structure de la table `game_answer`
--

DROP TABLE IF EXISTS `game_answer`;
CREATE TABLE IF NOT EXISTS `game_answer` (
  `id` int NOT NULL AUTO_INCREMENT,
  `session_id` int NOT NULL,
  `question_id` int NOT NULL,
  `selected_option_id` int NOT NULL,
  `is_correct` tinyint(1) NOT NULL,
  `answered_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_answer_session` (`session_id`),
  KEY `fk_answer_question` (`question_id`),
  KEY `fk_answer_option` (`selected_option_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `game_session`
--

DROP TABLE IF EXISTS `game_session`;
CREATE TABLE IF NOT EXISTS `game_session` (
  `id` int NOT NULL AUTO_INCREMENT,
  `player_id` int NOT NULL,
  `score` int NOT NULL DEFAULT '0',
  `lives` int NOT NULL DEFAULT '3',
  `current_level` int NOT NULL DEFAULT '1',
  `status` enum('IN_PROGRESS','WON','LOST') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'IN_PROGRESS',
  `started_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `finished_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_session_player` (`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `level`
--

DROP TABLE IF EXISTS `level`;
CREATE TABLE IF NOT EXISTS `level` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `level_number` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `level_number` (`level_number`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `level`
--

INSERT INTO `level` (`id`, `title`, `description`, `level_number`) VALUES
(1, 'Le mot de passe secret', 'Identifier les caractéristiques d un mot de passe sécurisé.', 1),
(2, 'Détecter le phishing', 'Reconnaître les messages électroniques frauduleux.', 2),
(3, 'Le code de sécurité', 'Résoudre une énigme logique pour déverrouiller une porte.', 3);

-- --------------------------------------------------------

--
-- Structure de la table `player`
--

DROP TABLE IF EXISTS `player`;
CREATE TABLE IF NOT EXISTS `player` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nickname` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nickname` (`nickname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `question`
--

DROP TABLE IF EXISTS `question`;
CREATE TABLE IF NOT EXISTS `question` (
  `id` int NOT NULL AUTO_INCREMENT,
  `level_id` int NOT NULL,
  `question_text` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `explanation` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  KEY `fk_question_level` (`level_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `question`
--

INSERT INTO `question` (`id`, `level_id`, `question_text`, `explanation`) VALUES
(1, 1, 'Quel est le mot de passe le plus sécurisé ?', 'Un mot de passe robuste doit être long et contenir différents types de caractères.'),
(2, 1, 'Pourquoi faut-il éviter d utiliser le même mot de passe sur plusieurs sites ?', 'Si un compte est compromis, les autres comptes utilisant le même mot de passe deviennent vulnérables.'),
(3, 1, 'Quelle est la meilleure pratique pour protéger ses mots de passe ?', 'Un gestionnaire de mots de passe permet de stocker des mots de passe uniques et complexes.'),
(4, 2, 'Vous recevez un email vous demandant de confirmer immédiatement vos coordonnées bancaires via un lien. Que faites-vous ?', 'Il faut éviter de cliquer sur les liens suspects et vérifier directement auprès de l organisme concerné.'),
(5, 2, 'Quel élément peut indiquer une tentative de phishing ?', 'Une adresse email inconnue ou suspecte peut être un signe de phishing.'),
(6, 2, 'Un inconnu vous demande votre mot de passe par email. Que devez-vous faire ?', 'Ne communiquez jamais votre mot de passe par email.'),
(7, 3, 'Quel est le rôle principal d un pare-feu informatique ?', 'Un pare-feu contrôle les communications réseau afin de bloquer les accès non autorisés.'),
(8, 3, 'Que signifie HTTPS dans une adresse web ?', 'HTTPS permet de sécuriser les échanges entre le navigateur et le serveur grâce au chiffrement.'),
(9, 3, 'Quel comportement est recommandé lorsqu on utilise un Wi-Fi public ?', 'Il faut éviter les opérations sensibles sur un réseau Wi-Fi public non sécurisé.');

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `answer_option`
--
ALTER TABLE `answer_option`
  ADD CONSTRAINT `fk_option_question` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `game_answer`
--
ALTER TABLE `game_answer`
  ADD CONSTRAINT `fk_answer_option` FOREIGN KEY (`selected_option_id`) REFERENCES `answer_option` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_answer_question` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_answer_session` FOREIGN KEY (`session_id`) REFERENCES `game_session` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `game_session`
--
ALTER TABLE `game_session`
  ADD CONSTRAINT `fk_session_player` FOREIGN KEY (`player_id`) REFERENCES `player` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `question`
--
ALTER TABLE `question`
  ADD CONSTRAINT `fk_question_level` FOREIGN KEY (`level_id`) REFERENCES `level` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
