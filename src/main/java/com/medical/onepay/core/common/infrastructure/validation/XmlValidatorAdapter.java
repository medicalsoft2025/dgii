package com.medical.onepay.core.common.infrastructure.validation;

import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

@Component
public class XmlValidatorAdapter {

    /**
     * Valida un contenido XML en formato String contra un archivo de esquema XSD.
     *
     * @param xmlContent El String que contiene el XML a validar.
     * @param xsdPath    La ruta completa al archivo .xsd.
     * @throws SAXException Si el XML no cumple con las reglas del esquema.
     * @throws IOException  Si ocurre un error al leer el XML o el archivo XSD.
     */
    public void validate(String xmlContent, String xsdPath) throws SAXException, IOException {
        // 1. Crear una fábrica de esquemas para el lenguaje XSD.
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        // 2. Cargar el esquema desde el archivo XSD.
        Schema schema = factory.newSchema(new File(xsdPath));

        // 3. Crear un validador a partir del esquema.
        Validator validator = schema.newValidator();

        // 4. Validar el contenido XML. Se usa un StringReader para validar
        // el XML directamente desde el String sin necesidad de crear un archivo.
        validator.validate(new StreamSource(new StringReader(xmlContent)));
    }
}
