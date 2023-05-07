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
package org.jreleaser.sdk.bluesky;

import org.jreleaser.bundle.RB;
import org.jreleaser.logging.JReleaserLogger;
import org.jreleaser.sdk.bluesky.api.BlueskyAPI;
import org.jreleaser.sdk.commons.RestAPIException;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.jreleaser.util.StringUtils.requireNonBlank;

/**
 * @author Tom cools
 * @since 1.7.0
 */
public class BlueskySdk {
    private final JReleaserLogger logger;
    private final BlueskyAPI api;
    private final boolean dryrun;

    private BlueskySdk(JReleaserLogger logger,
                       String host,
                       int connectTimeout,
                       int readTimeout,
                        boolean dryrun) {
        requireNonNull(logger, "'logger' must not be null");
        requireNonBlank(host, "'host' must not be blank");

        this.logger = logger;
        this.dryrun = dryrun;

        //TODO setup api
        api = null;

        this.logger.debug(RB.$("workflow.dryrun"), dryrun);
    }

    public void skeet(List<String> statuses) throws BlueskyException {
        //TODO bejug placeholder
    }

    private void wrap(Runnable runnable) throws BlueskyException {
        try {
            if (!dryrun) runnable.run();
        } catch (RestAPIException e) {
            logger.trace(e);
            throw new BlueskyException(RB.$("sdk.operation.failed", "Bluesky"), e);
        }
    }

    public static Builder builder(JReleaserLogger logger) {
        return new Builder(logger);
    }

    public static class Builder {
        private final JReleaserLogger logger;
        private boolean dryrun;
        private String host;
        private int connectTimeout = 20;
        private int readTimeout = 60;

        private Builder(JReleaserLogger logger) {
            this.logger = requireNonNull(logger, "'logger' must not be null");
        }

        public Builder dryrun(boolean dryrun) {
            this.dryrun = dryrun;
            return this;
        }

        public Builder host(String host) {
            this.host = requireNonBlank(host, "'host' must not be blank").trim();
            return this;
        }

        public Builder connectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder readTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        private void validate() {
            requireNonBlank(host, "'host' must not be blank");
        }

        public BlueskySdk build() {
            validate();

            return new BlueskySdk(
                logger,
                host,
                connectTimeout,
                readTimeout,
                dryrun);
        }
    }

}
