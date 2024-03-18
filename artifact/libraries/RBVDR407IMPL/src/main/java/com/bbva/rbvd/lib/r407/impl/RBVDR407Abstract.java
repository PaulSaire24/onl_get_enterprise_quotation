package com.bbva.rbvd.lib.r407.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.library.AbstractLibrary;
import com.bbva.elara.utility.api.connector.APIConnector;
import com.bbva.elara.utility.api.connector.APIConnectorBuilder;
import com.bbva.pisd.lib.r014.PISDR014;
import com.bbva.pisd.lib.r401.PISDR401;
import com.bbva.pisd.lib.r402.PISDR402;
import com.bbva.pisd.lib.r601.PISDR601;
import com.bbva.rbvd.lib.r407.RBVDR407;

/**
 * This class automatically defines the libraries and utilities that it will use.
 */
public abstract class RBVDR407Abstract extends AbstractLibrary implements RBVDR407 {

	protected ApplicationConfigurationService applicationConfigurationService;

	protected APIConnector externalApiConnector;

	protected APIConnectorBuilder apiConnectorBuilder;

	protected PISDR401 pisdR401;

	protected PISDR014 pisdR014;

	protected PISDR601 pisdR601;

	protected PISDR402 pisdR402;


	/**
	* @param applicationConfigurationService the this.applicationConfigurationService to set
	*/
	public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
		this.applicationConfigurationService = applicationConfigurationService;
	}

	/**
	* @param externalApiConnector the this.externalApiConnector to set
	*/
	public void setExternalApiConnector(APIConnector externalApiConnector) {
		this.externalApiConnector = externalApiConnector;
	}

	/**
	* @param apiConnectorBuilder the this.apiConnectorBuilder to set
	*/
	public void setApiConnectorBuilder(APIConnectorBuilder apiConnectorBuilder) {
		this.apiConnectorBuilder = apiConnectorBuilder;
	}

	/**
	* @param pisdR401 the this.pisdR401 to set
	*/
	public void setPisdR401(PISDR401 pisdR401) {
		this.pisdR401 = pisdR401;
	}

	/**
	* @param pisdR014 the this.pisdR014 to set
	*/
	public void setPisdR014(PISDR014 pisdR014) {
		this.pisdR014 = pisdR014;
	}

	/**
	* @param pisdR601 the this.pisdR601 to set
	*/
	public void setPisdR601(PISDR601 pisdR601) {
		this.pisdR601 = pisdR601;
	}

	/**
	* @param pisdR402 the this.pisdR402 to set
	*/
	public void setPisdR402(PISDR402 pisdR402) {
		this.pisdR402 = pisdR402;
	}

}