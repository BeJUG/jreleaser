/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2020-2023 The JReleaser authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jreleaser.model.internal.validation.assemble;

import org.jreleaser.bundle.RB;
import org.jreleaser.model.Archive;
import org.jreleaser.model.internal.JReleaserContext;
import org.jreleaser.model.internal.assemble.NativeImageAssembler;
import org.jreleaser.model.internal.common.Artifact;
import org.jreleaser.util.Errors;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.jreleaser.model.Constants.KEY_ARCHIVE_FORMAT;
import static org.jreleaser.util.StringUtils.isNotBlank;

/**
 * @author Andres Almiray
 * @since 0.2.0
 */
public final class NativeImageAssemblerResolver {
    private NativeImageAssemblerResolver() {
        // noop
    }

    public static void resolveNativeImageOutputs(JReleaserContext context, Errors errors) {
        List<NativeImageAssembler> activeNativeImages = context.getModel().getAssemble().getActiveNativeImages();
        if (!activeNativeImages.isEmpty()) context.getLogger().debug("assemble.nativeImage");

        for (NativeImageAssembler nativeImage : activeNativeImages) {
            if (nativeImage.isExported()) resolveNativeImageOutputs(context, nativeImage, errors);
        }
    }

    private static void resolveNativeImageOutputs(JReleaserContext context, NativeImageAssembler nativeImage, Errors errors) {
        Path baseOutputDirectory = context.getAssembleDirectory()
            .resolve(nativeImage.getName())
            .resolve(nativeImage.getType());

        String imageName = nativeImage.getResolvedImageName(context);
        if (isNotBlank(nativeImage.getImageNameTransform())) {
            imageName = nativeImage.getResolvedImageNameTransform(context);
        }

        for (Artifact graalJdk : nativeImage.getGraalJdks()) {
            if (!context.isPlatformSelected(graalJdk)) continue;

            String platform = graalJdk.getPlatform();
            String platformReplaced = nativeImage.getPlatform().applyReplacements(platform);
            String str = graalJdk.getExtraProperties()
                .getOrDefault(KEY_ARCHIVE_FORMAT, nativeImage.getArchiveFormat())
                .toString();
            Archive.Format archiveFormat = Archive.Format.of(str);

            Path image = baseOutputDirectory
                .resolve(imageName + "-" + platformReplaced + "." + archiveFormat.extension())
                .toAbsolutePath();

            if (!Files.exists(image)) {
                errors.assembly(RB.$("validation_missing_assembly",
                    nativeImage.getType(), nativeImage.getName(), nativeImage.getName()));
            } else {
                Artifact artifact = Artifact.of(image, platform);
                artifact.setExtraProperties(nativeImage.getExtraProperties());
                artifact.activate();
                nativeImage.addOutput(artifact);
            }
        }
    }
}
