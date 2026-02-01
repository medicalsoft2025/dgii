package com.medical.onepay.core.common.infrastructure.jaxb;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Adaptador JAXB para asegurar que los valores BigDecimal se serialicen a XML
 * con exactamente dos decimales (ej. "6000.00").
 */
public class BigDecimalAdapter extends XmlAdapter<String, BigDecimal> {

    private final DecimalFormat df = new DecimalFormat("0.00");

    /**
     * Convierte un String del XML a un objeto BigDecimal.
     */
    @Override
    public BigDecimal unmarshal(String v) throws Exception {
        if (v == null || v.isEmpty()) {
            return null;
        }
        return new BigDecimal(v);
    }

    /**
     * Convierte un objeto BigDecimal al formato String con dos decimales para el XML.
     */
    @Override
    public String marshal(BigDecimal v) throws Exception {
        if (v == null) {
            return null;
        }
        // Sincronizamos el DecimalFormat para seguridad en entornos concurrentes, aunque aquí es poco probable.
        synchronized (df) {
            return df.format(v.setScale(2, RoundingMode.HALF_UP));
        }
    }
}
