package fr.univcotedazur.skimaster.monitoring.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;

@Entity
public class PanelConnection {

        @Id
        private String id;

        @NotBlank
        private String URI;

        protected PanelConnection() {}

        public PanelConnection(String id, String URI) {
            this.id = id;
            this.URI = URI;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setURI(String URI) {
            this.URI = URI;
        }

        public String getId() {
            return id;
        }

        public String getURI() {
            return URI;
        }

}

