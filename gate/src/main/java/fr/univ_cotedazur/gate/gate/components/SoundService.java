package fr.univ_cotedazur.gate.gate.components;

import fr.univ_cotedazur.gate.gate.entities.Sound;
import org.springframework.stereotype.Service;

@Service
public class SoundService {

    public SoundService() {}

    public String playValidSound(Sound sound) {
        return switch (sound) {
            case Sound.LOW_SOUND -> {
                System.out.println("Sound: Low sound");
                yield "Sound: Low sound";
            }
            case Sound.HIGH_SOUND -> {
                System.out.println("Sound : High sound");
                yield "Sound: High sound";
            }
        };
    }

    public String playInvalidSound() {
        System.out.println("Sound : Invalid Sound");
        return "Sound : Invalid Sound";
    }
}
