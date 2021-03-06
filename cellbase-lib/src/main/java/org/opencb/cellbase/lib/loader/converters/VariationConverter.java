/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.cellbase.lib.loader.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.opencb.biodata.models.variation.Variation;
import org.opencb.cellbase.lib.MongoDBCollectionConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by imedina on 31/08/14.
 */
@Deprecated
public class VariationConverter extends MongoDBTypeConverter<Variation, Document> {

    private int chunkSize;
    private String chunkIdSuffix;


    public VariationConverter() {
        this(MongoDBCollectionConfiguration.VARIATION_CHUNK_SIZE);
    }

    public VariationConverter(int chunkSize) {
        // Parent class initializes and configures Jackson objects
        super();

        this.chunkSize = chunkSize;
        this.chunkIdSuffix = this.chunkSize / 1000 + "k";
    }

    @Override
    public Document convertToStorageSchema(Variation variation) {
        Document document = null;
        try {
            document = (Document) JSON.parse(jsonObjectWriter.writeValueAsString(variation));

            int chunkStart = (variation.getStart()) / chunkSize;
            int chunkEnd = (variation.getEnd()) / chunkSize;

            List<String> chunkIdsList = new ArrayList<>(chunkEnd - chunkStart + 1);
            for (int i = chunkStart; i <= chunkEnd; i++) {
                chunkIdsList.add(variation.getChromosome() + "_" + i + "_" + chunkIdSuffix);
            }

            document.put("chunkIds", chunkIdsList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return document;
    }

    @Override
    public Variation convertToDataModel(Document dbObject) {
        return null;
    }
}
