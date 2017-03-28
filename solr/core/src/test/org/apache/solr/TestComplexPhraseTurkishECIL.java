/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.solr.search;

import org.apache.solr.util.AbstractSolrTestCase;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestComplexPhraseTurkishECIL extends AbstractSolrTestCase {

    @BeforeClass
    public static void beforeClass() throws Exception {
        System.setProperty("enable.update.log", "false");
        initCore("solrconfig-minimal.xml","schema-folding-tr.xml");
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        System.clearProperty("enable.update.log");
        super.tearDown();
    }
    @Test
    public void testDefaultField() {

        assertU(adoc("content_turkish", "Acun Ilıcalı hastalandı", "id", "11"));
        assertU(adoc("content_turkish", "ACUN ılıcalI'nın şirketi", "id", "12"));
        assertU(adoc("content_turkish", "TV8 yapımcısı Acun ILICALI'ın şirketi", "id", "13"));
        assertU(adoc("content_turkish", "hazırlanan ve Oya Küçümen, Acun Ilıcalı, Saffet Üçüncü ", "id", "14"));
        assertU(commit());
        assertU(optimize());

        assertQ("lowercase",
                req("q","{!complexphrase}\"Acun ılıcalı*\"","qt","/select","df","content_turkish"),
                "//doc[./str[@name='id']='11']",
                "//doc[./str[@name='id']='12']",
                "//doc[./str[@name='id']='13']",
                "//doc[./str[@name='id']='14']",
                "//result[@numFound='4']"
        );
        assertQ("lowercase",
                req("q","{!complexphrase}\"Acun ILICALI*\"","qt","/select","df","content_turkish"),
                "//doc[./str[@name='id']='11']",
                "//doc[./str[@name='id']='12']",
                "//doc[./str[@name='id']='13']",
                "//doc[./str[@name='id']='14']",
                "//result[@numFound='4']"
        );

    }

}
