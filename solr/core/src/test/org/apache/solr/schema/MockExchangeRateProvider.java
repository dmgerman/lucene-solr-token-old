begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|HashSet
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|common
operator|.
name|SolrException
operator|.
name|ErrorCode
import|;
end_import
begin_comment
comment|/**  * Simple mock provider with fixed rates and some assertions  */
end_comment
begin_class
DECL|class|MockExchangeRateProvider
specifier|public
class|class
name|MockExchangeRateProvider
implements|implements
name|ExchangeRateProvider
block|{
DECL|field|map
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Double
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
static|static
block|{
name|map
operator|.
name|put
argument_list|(
literal|"USD,EUR"
argument_list|,
literal|0.8
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"EUR,USD"
argument_list|,
literal|1.2
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"USD,NOK"
argument_list|,
literal|5.0
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"NOK,USD"
argument_list|,
literal|0.2
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"EUR,NOK"
argument_list|,
literal|10.0
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"NOK,EUR"
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
block|}
DECL|field|gotArgs
specifier|private
name|boolean
name|gotArgs
init|=
literal|false
decl_stmt|;
DECL|field|gotLoader
specifier|private
name|boolean
name|gotLoader
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
DECL|method|getExchangeRate
specifier|public
name|double
name|getExchangeRate
parameter_list|(
name|String
name|sourceCurrencyCode
parameter_list|,
name|String
name|targetCurrencyCode
parameter_list|)
block|{
comment|//    System.out.println("***** getExchangeRate("+sourceCurrencyCode+targetCurrencyCode+")");
if|if
condition|(
name|sourceCurrencyCode
operator|.
name|equals
argument_list|(
name|targetCurrencyCode
argument_list|)
condition|)
return|return
literal|1.0
return|;
name|Double
name|result
init|=
name|map
operator|.
name|get
argument_list|(
name|sourceCurrencyCode
operator|+
literal|","
operator|+
name|targetCurrencyCode
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|NOT_FOUND
argument_list|,
literal|"No exchange rate found for the pair "
operator|+
name|sourceCurrencyCode
operator|+
literal|","
operator|+
name|targetCurrencyCode
argument_list|)
throw|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|listAvailableCurrencies
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|listAvailableCurrencies
parameter_list|()
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|currenciesPairs
init|=
name|map
operator|.
name|keySet
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|returnSet
decl_stmt|;
name|returnSet
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|c
range|:
name|currenciesPairs
control|)
block|{
name|String
index|[]
name|pairs
init|=
name|c
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|returnSet
operator|.
name|add
argument_list|(
name|pairs
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|returnSet
operator|.
name|add
argument_list|(
name|pairs
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|returnSet
return|;
block|}
annotation|@
name|Override
DECL|method|reload
specifier|public
name|boolean
name|reload
parameter_list|()
throws|throws
name|SolrException
block|{
assert|assert
operator|(
name|gotArgs
operator|==
literal|true
operator|)
assert|;
assert|assert
operator|(
name|gotLoader
operator|==
literal|true
operator|)
assert|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
assert|assert
operator|(
name|args
operator|.
name|get
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"bar"
argument_list|)
operator|)
assert|;
name|gotArgs
operator|=
literal|true
expr_stmt|;
name|args
operator|.
name|remove
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|)
throws|throws
name|SolrException
block|{
assert|assert
operator|(
name|loader
operator|!=
literal|null
operator|)
assert|;
name|gotLoader
operator|=
literal|true
expr_stmt|;
assert|assert
operator|(
name|gotArgs
operator|==
literal|true
operator|)
assert|;
block|}
block|}
end_class
end_unit
