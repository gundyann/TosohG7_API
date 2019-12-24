
/**
 *date: 23.11.2019   -  time: 18:15:36
 *user: yanng   -  yann.gund@gmx.ch
 *
 */
package services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import enums.OperationMode;
import enums.SamplePosition;

/**
 * The Class OperationModeService holds all Informations about the values that get transmitted by the Tosoh G7 modality.
 * 
 * The Tosoh supports three modes. In each of these the values in the G7 format have a different value.
 * 
 * This class maps the operationMode of the Tosoh to a List containing the measurement units of the 10 values of the bloodanalysis.
 */
public class OperationModeService {

	/** The operation mode service that gets returned. This class is implemented in the Singleton pattern. */
	private static OperationModeService operationModeService = null;

	/**
	 * The operation modes. For further information see the manual of the TosohG7
	 * API
	 */
	private Map<Integer, OperationMode> operationModes = new HashMap<>();

	/**
	 * The sample positions. For further information see the manual of the TosohG7
	 * API
	 */
	private Map<Integer, SamplePosition> samplePositions = new HashMap<>();

	/** This maps a operationMode to a List representing the 10 measurement units of a measurement in the G7 format.
	 *  For further information see the manual of the TosohG7 API*/
	private Map<OperationMode, List<String>> listOfParamsAccordingToOperationMode = new HashMap<>();

	/**
	 * Gets the operation mode service.
	 *
	 * @return the operation mode service
	 */
	public static OperationModeService getOperationModeService() {
		if (operationModeService == null) {
			operationModeService = new OperationModeService();
		}
		return operationModeService;
	}

	/**
	 * Instantiates a new operation mode service.
	 */
	private OperationModeService() {
		this.initializeParams();
	}
	
	/**
	 * Gets the operation mode.
	 * this maps the integer that represents the operation mode to a typesafe Enumeration of the type <code>OperationMode</code>.
	 *
	 * @param index - integer representing the operation mode according to the API specifications.
	 * @return the operation mode
	 */
	public OperationMode getOperationMode(int index) {
		return this.operationModes.get(index);
	}

	/**
	 * Gets the sample position.
	 * This maps the integer that represents the sample position to a typesafe Enumeration of the type <code>SamplePosition</code>.
	 * 
	 *
	 * @param index  - integer representing the sample position according to the API specifications.
	 * @return the sample position
	 */
	public SamplePosition getSamplePosition(int index) {
		return this.samplePositions.get(index);
	}
	
	/**
	 * This returns the list of measurement units based on the <code>OperationMode</code>.
	 *
	 * @param mode the <code>OperationMode</code> of the measurement.
	 * @return the list of params according to operation mode
	 */
	public List<String> getListOfParamsAccordingToOperationMode(OperationMode mode){
		return this.listOfParamsAccordingToOperationMode.get(mode);
	}
	
	/**
	 * Initialize params.
	 * 
	 * This method sets up all maps that are used to map integers to Enumerations.
	 * 
	 * 
	 */
	private void initializeParams() {

		this.operationModes.put(0, OperationMode.STD);
		this.operationModes.put(1, OperationMode.VAR);
		this.operationModes.put(2, OperationMode.Beta_Thalassemia);

		this.samplePositions.put(0, SamplePosition.STAT);
		this.samplePositions.put(1, SamplePosition.Transport);
		this.samplePositions.put(2, SamplePosition.Loader);

		List<String> modusSTD = Arrays.asList("FP%", "A1a%", "A1b%", "F%", "LA1c%", "SA1c%", "A0%", "---", "---",
				"Total A1%");
		List<String> modusVAR = Arrays.asList("A1a%", "A1b%", "F%", "LA1c%", "SA1c%", "A0%", "H-V0%", "H-V1%", "H-V2%",
				"Total A1%");
		List<String> modusBetaThalassemia = Arrays.asList("HbF%", "A0%", "HbA2%", "HbD+%", "HbS+%", "HbC+%", "---",
				"---", "---", "---");
		this.listOfParamsAccordingToOperationMode.put(OperationMode.STD, modusSTD);
		this.listOfParamsAccordingToOperationMode.put(OperationMode.VAR, modusVAR);
		this.listOfParamsAccordingToOperationMode.put(OperationMode.Beta_Thalassemia, modusBetaThalassemia);
	}

}
