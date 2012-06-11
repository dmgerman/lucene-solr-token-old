begin_unit
begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
operator|.
name|ResourceLoader
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
name|common
operator|.
name|SolrException
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
name|core
operator|.
name|SolrResourceLoader
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_comment
comment|/**  * Tests currency field type.  */
end_comment
begin_class
DECL|class|OpenExchangeRatesOrgProviderTest
specifier|public
class|class
name|OpenExchangeRatesOrgProviderTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|oerp
name|OpenExchangeRatesOrgProvider
name|oerp
decl_stmt|;
DECL|field|loader
name|ResourceLoader
name|loader
decl_stmt|;
DECL|field|emptyParams
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|emptyParams
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|mockParams
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mockParams
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|mockParams
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
empty_stmt|;
name|mockParams
operator|.
name|put
argument_list|(
name|OpenExchangeRatesOrgProvider
operator|.
name|PARAM_RATES_FILE_LOCATION
argument_list|,
literal|"open-exchange-rates.json"
argument_list|)
expr_stmt|;
name|oerp
operator|=
operator|new
name|OpenExchangeRatesOrgProvider
argument_list|()
expr_stmt|;
name|loader
operator|=
operator|new
name|SolrResourceLoader
argument_list|(
literal|"solr"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInit
specifier|public
name|void
name|testInit
parameter_list|()
throws|throws
name|Exception
block|{
name|oerp
operator|.
name|init
argument_list|(
name|emptyParams
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Wrong default url"
argument_list|,
name|oerp
operator|.
name|ratesFileLocation
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"http://openexchangerates.org/latest.json"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Wrong default interval"
argument_list|,
name|oerp
operator|.
name|refreshInterval
operator|==
literal|1440
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
name|OpenExchangeRatesOrgProvider
operator|.
name|PARAM_RATES_FILE_LOCATION
argument_list|,
literal|"http://foo.bar/baz"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|OpenExchangeRatesOrgProvider
operator|.
name|PARAM_REFRESH_INTERVAL
argument_list|,
literal|"100"
argument_list|)
expr_stmt|;
name|oerp
operator|.
name|init
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Wrong param set url"
argument_list|,
name|oerp
operator|.
name|ratesFileLocation
operator|.
name|equals
argument_list|(
literal|"http://foo.bar/baz"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Wrong param interval"
argument_list|,
name|oerp
operator|.
name|refreshInterval
operator|==
literal|100
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testList
specifier|public
name|void
name|testList
parameter_list|()
block|{
name|oerp
operator|.
name|init
argument_list|(
name|mockParams
argument_list|)
expr_stmt|;
name|oerp
operator|.
name|inform
argument_list|(
name|loader
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|159
argument_list|,
name|oerp
operator|.
name|listAvailableCurrencies
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetExchangeRate
specifier|public
name|void
name|testGetExchangeRate
parameter_list|()
block|{
name|oerp
operator|.
name|init
argument_list|(
name|mockParams
argument_list|)
expr_stmt|;
name|oerp
operator|.
name|inform
argument_list|(
name|loader
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|5.73163
operator|==
name|oerp
operator|.
name|getExchangeRate
argument_list|(
literal|"USD"
argument_list|,
literal|"NOK"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReload
specifier|public
name|void
name|testReload
parameter_list|()
block|{
name|oerp
operator|.
name|init
argument_list|(
name|mockParams
argument_list|)
expr_stmt|;
name|oerp
operator|.
name|inform
argument_list|(
name|loader
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|oerp
operator|.
name|reload
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"USD"
argument_list|,
name|oerp
operator|.
name|rates
operator|.
name|getBaseCurrency
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Long
argument_list|(
literal|1332070464L
argument_list|)
argument_list|,
operator|new
name|Long
argument_list|(
name|oerp
operator|.
name|rates
operator|.
name|getTimestamp
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|SolrException
operator|.
name|class
argument_list|)
DECL|method|testNoInit
specifier|public
name|void
name|testNoInit
parameter_list|()
block|{
name|oerp
operator|.
name|getExchangeRate
argument_list|(
literal|"ABC"
argument_list|,
literal|"DEF"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have thrown exception if not initialized"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
