package org.cryptomator.domain.usecases;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.cryptomator.domain.exception.BackendException;
import org.cryptomator.domain.exception.FatalBackendException;
import org.cryptomator.domain.exception.license.DesktopSupporterCertificateException;
import org.cryptomator.domain.exception.license.LicenseNotValidException;
import org.cryptomator.domain.exception.license.NoLicenseAvailableException;
import org.cryptomator.generator.Parameter;
import org.cryptomator.generator.UseCase;
import org.cryptomator.util.SharedPreferencesHandler;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@UseCase
public class DoLicenseCheck {

	private static final String DESKTOP_SUPPORTER_CERTIFICATE_PUB_KEY = "MIGbMBAGByqGSM49AgEGBSuBBAAjA4GGAAQB7NfnqiZbg2KTmoflmZ71PbXru7oW" + //
			"fmnV2yv3eDjlDfGruBrqz9TtXBZV/eYWt31xu1osIqaT12lKBvZ511aaAkIBeOEV" + //
			"gwcBIlJr6kUw7NKzeJt7r2rrsOyQoOG2nWc/Of/NBqA3mIZRHk5Aq1YupFdD26QE" + //
			"r0DzRyj4ixPIt38CQB8=";
	private final SharedPreferencesHandler sharedPreferencesHandler;
	private String license;

	DoLicenseCheck(final SharedPreferencesHandler sharedPreferencesHandler, @Parameter final String license) {
		this.sharedPreferencesHandler = sharedPreferencesHandler;
		this.license = license;
	}

	public LicenseCheck execute() throws BackendException {
		return new LicenseCheck() {
                    public String mail() {
                        return "";
                    }
                };
	}

	private String useLicenseOrRetrieveFromPreferences(String license) throws NoLicenseAvailableException {
		if (!license.isEmpty()) {
			return license;
		}
		String stored = sharedPreferencesHandler.licenseToken();
		if (stored.isEmpty()) {
			throw new NoLicenseAvailableException();
		}
		return stored;
	}

	private boolean isDesktopSupporterCertificate(String license) {
		try {
			AndroidLicenseVerifier.verify(license, DESKTOP_SUPPORTER_CERTIFICATE_PUB_KEY);
			return true;
		} catch (SignatureVerificationException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			return false;
		}
	}
}
