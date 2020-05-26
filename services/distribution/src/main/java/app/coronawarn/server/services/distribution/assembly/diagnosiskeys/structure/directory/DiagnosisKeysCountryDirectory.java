/*
 * Corona-Warn-App
 *
 * SAP SE and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package app.coronawarn.server.services.distribution.assembly.diagnosiskeys.structure.directory;

import app.coronawarn.server.services.distribution.assembly.component.CryptoProvider;
import app.coronawarn.server.services.distribution.assembly.diagnosiskeys.Export;
import app.coronawarn.server.services.distribution.assembly.diagnosiskeys.structure.directory.decorator.ExportBatchDecorator;
import app.coronawarn.server.services.distribution.assembly.diagnosiskeys.structure.directory.decorator.ExportBatchIndexingDecorator;
import app.coronawarn.server.services.distribution.assembly.structure.directory.IndexDirectoryOnDisk;
import app.coronawarn.server.services.distribution.assembly.structure.util.ImmutableStack;
import app.coronawarn.server.services.distribution.config.DistributionServiceConfig;
import app.coronawarn.server.services.distribution.persistence.domain.ExportBatch;
import app.coronawarn.server.services.distribution.persistence.domain.ExportBatchStatus;
import app.coronawarn.server.services.distribution.persistence.domain.ExportConfiguration;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class DiagnosisKeysCountryDirectory extends IndexDirectoryOnDisk<String> {

  private final Collection<Export> exports;
  private final CryptoProvider cryptoProvider;
  private final DistributionServiceConfig distributionServiceConfig;

  /**
   * Constructs a {@link DiagnosisKeysCountryDirectory} instance that represents the {@code .../country/:country/...}
   * portion of the diagnosis key directory structure.
   *
   * @param exports  The diagnosis keys processed in the contained sub directories.
   * @param cryptoProvider The {@link CryptoProvider} used for payload signing.
   */
  public DiagnosisKeysCountryDirectory(Collection<Export> exports,
                                       CryptoProvider cryptoProvider, DistributionServiceConfig distributionServiceConfig) {
    super(distributionServiceConfig.getApi().getCountryPath(), x ->
            Set.of(distributionServiceConfig.getApi().getCountryGermany()), Object::toString);;
    this.exports = exports;
    this.cryptoProvider = cryptoProvider;
    this.distributionServiceConfig = distributionServiceConfig;
  }

  @Override
  public void prepare(ImmutableStack<Object> indices) {
    // TODO read things here

    this.addWritableToAll(__ -> {
      DiagnosisKeysExportBatchDirectory exportBatchDirectory = new DiagnosisKeysExportBatchDirectory(exports,
              cryptoProvider, distributionServiceConfig);
      return decorateExportBatchDirectory(exportBatchDirectory);
    });
    super.prepare(indices);
  }

  private ExportBatchDecorator decorateExportBatchDirectory(
          DiagnosisKeysExportBatchDirectory exportBatchDirectory) {
    return new ExportBatchDecorator(new ExportBatchIndexingDecorator(exportBatchDirectory, distributionServiceConfig),
        cryptoProvider, distributionServiceConfig);
  }
}
