package com.example.invoiceapi.xml;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class DaneIdentyfikacyjne {

    @XmlElement(name = "NIP", namespace = "http://crd.gov.pl/wzor/2023/06/29/12648/")
    private String NIP;
}
