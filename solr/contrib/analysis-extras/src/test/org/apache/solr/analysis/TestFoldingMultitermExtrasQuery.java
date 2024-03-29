begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|SolrTestCaseJ4
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_class
DECL|class|TestFoldingMultitermExtrasQuery
specifier|public
class|class
name|TestFoldingMultitermExtrasQuery
extends|extends
name|SolrTestCaseJ4
block|{
DECL|method|getCoreName
specifier|public
name|String
name|getCoreName
parameter_list|()
block|{
return|return
literal|"basic"
return|;
block|}
annotation|@
name|BeforeClass
DECL|method|beforeTests
specifier|public
specifier|static
name|void
name|beforeTests
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|testHome
init|=
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|FileUtils
operator|.
name|copyDirectory
argument_list|(
name|getFile
argument_list|(
literal|"analysis-extras/solr"
argument_list|)
argument_list|,
name|testHome
argument_list|)
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig-icucollate.xml"
argument_list|,
literal|"schema-folding-extra.xml"
argument_list|,
name|testHome
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|idx
init|=
literal|1
decl_stmt|;
comment|// ICUFoldingFilterFactory
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|,
literal|"content_icufolding"
argument_list|,
literal|"BadMagicICUFolding"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|,
literal|"content_icufolding"
argument_list|,
literal|"RuÃ"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|,
literal|"content_icufolding"
argument_list|,
literal|"ÎÎÎªÎÎ£"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|,
literal|"content_icufolding"
argument_list|,
literal|"ÎÎ¬ÏÎ¿Ï"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|,
literal|"content_icufolding"
argument_list|,
literal|"rÃ©sumÃ©"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|,
literal|"content_icufolding"
argument_list|,
literal|"re\u0301sume\u0301"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|,
literal|"content_icufolding"
argument_list|,
literal|"ELÄ°F"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|,
literal|"content_icufolding"
argument_list|,
literal|"eli\u0307f"
argument_list|)
argument_list|)
expr_stmt|;
comment|// ICUNormalizer2FilterFactory
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|,
literal|"content_icunormalizer2"
argument_list|,
literal|"BadMagicICUFolding"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|,
literal|"content_icunormalizer2"
argument_list|,
literal|"RuÃ"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|,
literal|"content_icunormalizer2"
argument_list|,
literal|"ÎÎÎªÎÎ£"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|,
literal|"content_icunormalizer2"
argument_list|,
literal|"ÎÎ¬ÏÎ¿Ï"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|,
literal|"content_icunormalizer2"
argument_list|,
literal|"rÃ©sumÃ©"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|,
literal|"content_icunormalizer2"
argument_list|,
literal|"re\u0301sume\u0301"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|,
literal|"content_icunormalizer2"
argument_list|,
literal|"ELÄ°F"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|,
literal|"content_icunormalizer2"
argument_list|,
literal|"eli\u0307f"
argument_list|)
argument_list|)
expr_stmt|;
comment|// ICUTransformFilterFactory
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|,
literal|"content_icutransform"
argument_list|,
literal|"Ð Ð¾ÑÑÐ¸Ð¹ÑÐºÐ°Ñ"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testICUFolding
specifier|public
name|void
name|testICUFolding
parameter_list|()
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_icufolding:BadMagicicuFold*"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_icufolding:rU*"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_icufolding:Re*Me"
argument_list|)
argument_list|,
literal|"//result[@numFound='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_icufolding:RE\u0301su*"
argument_list|)
argument_list|,
literal|"//result[@numFound='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_icufolding:El*"
argument_list|)
argument_list|,
literal|"//result[@numFound='2']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testICUNormalizer2
specifier|public
name|void
name|testICUNormalizer2
parameter_list|()
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_icunormalizer2:BadMagicicuFold*"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_icunormalizer2:RU*"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_icunormalizer2:ÎÎ¬Ï*"
argument_list|)
argument_list|,
literal|"//result[@numFound='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_icunormalizer2:re\u0301Su*"
argument_list|)
argument_list|,
literal|"//result[@numFound='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_icunormalizer2:eL*"
argument_list|)
argument_list|,
literal|"//result[@numFound='2']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testICUTransform
specifier|public
name|void
name|testICUTransform
parameter_list|()
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_icutransform:Ð Ð¾ÑÑ*"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
