package com.example.invoiceapi.xml;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Podmiot1 {

    @XmlElement(name = "DaneIdentyfikacyjne", namespace = "http://crd.gov.pl/wzor/2023/06/29/12648/")
    private DaneIdentyfikacyjne daneIdentyfikacyjne;
}
