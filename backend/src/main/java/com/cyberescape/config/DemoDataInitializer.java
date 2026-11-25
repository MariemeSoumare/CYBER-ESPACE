package com.cyberescape.config;

import com.cyberescape.entity.AnswerOption;
import com.cyberescape.entity.Level;
import com.cyberescape.entity.Question;
import com.cyberescape.repository.AnswerOptionRepository;
import com.cyberescape.repository.LevelRepository;
import com.cyberescape.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoDataInitializer {

    @Bean
    CommandLineRunner initializeDemoData(
            LevelRepository levelRepository,
            QuestionRepository questionRepository,
            AnswerOptionRepository answerOptionRepository,
            @Value("${app.seed-demo-data:true}") boolean seedDemoData) {
        return args -> {
            if (!seedDemoData || levelRepository.count() > 0) {
                return;
            }

            Level levelOne = levelRepository.save(
                    new Level("Premiers réflexes", "Les bases pour protéger ses comptes.", 1));
            Level levelTwo = levelRepository.save(
                    new Level("Défense numérique", "Reconnaître les menaces courantes.", 2));

            Question passwordQuestion = questionRepository.save(new Question(
                    levelOne,
                    "Quel mot de passe est le plus difficile à deviner ?"));
            addOptions(answerOptionRepository, passwordQuestion,
                    new String[]{"12345678", "Cyber2026", "M@rie2026!X"},
                    2);

            Question linkQuestion = questionRepository.save(new Question(
                    levelOne,
                    "Vous recevez un lien inattendu vous demandant votre mot de passe. Que faites-vous ?"));
            addOptions(answerOptionRepository, linkQuestion,
                    new String[]{
                            "Je vérifie l'expéditeur et je ne saisis pas mon mot de passe via ce lien.",
                            "Je clique rapidement pour éviter la fermeture de mon compte.",
                            "Je transfère le lien à mes contacts."
                    },
                    0);

            Question wifiQuestion = questionRepository.save(new Question(
                    levelTwo,
                    "Quelle précaution est recommandée sur un Wi-Fi public ?"));
            addOptions(answerOptionRepository, wifiQuestion,
                    new String[]{
                            "Désactiver le verrouillage de l'appareil.",
                            "Éviter les opérations sensibles et utiliser une connexion sécurisée.",
                            "Partager ses identifiants avec le réseau."
                    },
                    1);

            Question updateQuestion = questionRepository.save(new Question(
                    levelTwo,
                    "Pourquoi installer les mises à jour de sécurité ?"));
            addOptions(answerOptionRepository, updateQuestion,
                    new String[]{
                            "Elles corrigent notamment des failles connues.",
                            "Elles rendent toujours les mots de passe inutiles.",
                            "Elles empêchent toute attaque future."
                    },
                    0);
        };
    }

    private void addOptions(
            AnswerOptionRepository repository,
            Question question,
            String[] options,
            int correctIndex) {
        for (int index = 0; index < options.length; index++) {
            repository.save(new AnswerOption(question, options[index], index == correctIndex));
        }
    }
}
