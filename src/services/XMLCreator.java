
/**
 *date: 30.10.2019   -  time: 09:21:36
 *user: yanng   -  yann.gund@gmx.ch
 *
 */
package services;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

import g7anbindung.BlutmessungG7;

// TODO: Auto-generated Javadoc
/**
 * The Class XMLCreator is used to create a XML-Document from
 * <code>BlutmessungG7</code> objects. The XML Document is based on the
 * BlutmessungXMLSchema.
 * 
 * It is used in the TosohG7 API to create a valid File that can be further used
 * in the orchestra enviroment.
 */
public class XMLCreator {

	/**
	 * Instantiates a new XML creator.
	 */
	public XMLCreator() {

	}

	/**
	 * Convert blutmessung to XML.
	 *
	 * @param data - the data in a List of <code>BlutmessungG7</code>
	 * @return the document in XML
	 */
	public Document convertBlutmessungToXML(BlutmessungG7 data) {

		Element blutmessung = new Element("bloodmeasurementG7");
		Document document = new Document(blutmessung).setRootElement(blutmessung);

		List<Element> messungenInElements = new ArrayList<>();

		Element bloodAnalysis = new Element("bloodAnalysis");
		bloodAnalysis.setAttribute(new Attribute("patientID", data.getPatientID()));
		bloodAnalysis.setAttribute(new Attribute("caseID", data.getCaseID()));
		bloodAnalysis.setAttribute(new Attribute("orderID", data.getOrderID()));

		Element measurements = new Element("measurements");

		Element measurementHbA1c = new Element("measurementHbA1c");
		measurementHbA1c.addContent(new Element("description").setText("Hba1c"));
		Element valueofMeasurement = new Element("value").setAttribute("unitOfMeasurement", "%").setAttribute("rangeOfValues","< 6.5");
		Element valueofMeasurement_mgdl = new Element("value").setAttribute("unitOfMeasurement", "mg/dL").setAttribute("rangeOfValues","< 132");
		Element valueofMeasurement_mmoll = new Element("value").setAttribute("unitOfMeasurement", "mmol/L").setAttribute("rangeOfValues","< 7.3");

		valueofMeasurement.setText(Double.toString(data.getHba1c()));
		if (data.getHba1c() <= 0) {
			valueofMeasurement_mgdl.setText("0.0");
			valueofMeasurement_mmoll.setText("0.0");
		} else {
			valueofMeasurement_mgdl.setText(Long.toString(Math.round((data.getHba1c() * 28.7) - 46.7)));
			valueofMeasurement_mmoll
					.setText(Double.toString(Math.round(((data.getHba1c() * 1.59) - 2.59) * 10) / 10.0));
		}
		measurementHbA1c.addContent(valueofMeasurement);
		measurementHbA1c.addContent(valueofMeasurement_mgdl);
		measurementHbA1c.addContent(valueofMeasurement_mmoll);

		measurements.addContent(measurementHbA1c);

		bloodAnalysis.addContent(measurements);
		messungenInElements.add(bloodAnalysis);

		blutmessung.addContent(messungenInElements);

		return document;
	}
}
