package org.fiware.impress.configuration;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Data;

import java.net.URL;

/**
 * General properties to be used for the server.
 */
@ConfigurationProperties("general")
@Data
public class GeneralProperties {

		/**
		 * ContextUrl for the service to use.
		 */
		private URL contextUrl;

		/**
		 * Tenant to be used by the contract server.
		 */
		private String tenant = null;

		private String downloadLinkTemplate = "http://localhost:9080/invoice/%s/pdf";
}
