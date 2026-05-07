package fr.univcotedazur.panel.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.springframework.lang.NonNull;

@Entity
public class PanelGateStatus {

    @Id
    @NotBlank
    private String gateName;

    @Enumerated(EnumType.STRING)
    @NonNull
    private GateStatus status;

    private String detail;

    public PanelGateStatus(){}

    public PanelGateStatus(String gateName, GateStatus status, String detail){
        this.gateName = gateName;
        this.status = status;
        this.detail = detail;
    }

    public String getName(){
        return gateName;
    }

    public GateStatus getStatus(){ return status; }

    public void setStatus(GateStatus status){ this.status = status; }

    public String getDetail(){
        return detail;
    }

    public void setDetail(String detail){
        this.detail = detail;
    }

}
