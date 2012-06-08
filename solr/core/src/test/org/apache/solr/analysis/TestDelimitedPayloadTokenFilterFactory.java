begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|StringReader
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
name|BaseTokenStreamTestCase
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
name|MockTokenizer
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
name|TokenStream
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
name|payloads
operator|.
name|DelimitedPayloadTokenFilter
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
name|payloads
operator|.
name|FloatEncoder
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
name|payloads
operator|.
name|PayloadHelper
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
name|tokenattributes
operator|.
name|PayloadAttribute
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
name|core
operator|.
name|SolrResourceLoader
import|;
end_import
begin_class
DECL|class|TestDelimitedPayloadTokenFilterFactory
specifier|public
class|class
name|TestDelimitedPayloadTokenFilterFactory
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testEncoder
specifier|public
name|void
name|testEncoder
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
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
name|args
operator|.
name|put
argument_list|(
name|DelimitedPayloadTokenFilterFactory
operator|.
name|ENCODER_ATTR
argument_list|,
literal|"float"
argument_list|)
expr_stmt|;
name|DelimitedPayloadTokenFilterFactory
name|factory
init|=
operator|new
name|DelimitedPayloadTokenFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|ResourceLoader
name|loader
init|=
operator|new
name|SolrResourceLoader
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|factory
operator|.
name|inform
argument_list|(
name|loader
argument_list|)
expr_stmt|;
name|TokenStream
name|input
init|=
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"the|0.1 quick|0.1 red|0.1"
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|DelimitedPayloadTokenFilter
name|tf
init|=
name|factory
operator|.
name|create
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|tf
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|tf
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|PayloadAttribute
name|payAttr
init|=
name|tf
operator|.
name|getAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"payAttr is null and it shouldn't be"
argument_list|,
name|payAttr
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|byte
index|[]
name|payData
init|=
name|payAttr
operator|.
name|getPayload
argument_list|()
operator|.
name|bytes
decl_stmt|;
name|assertTrue
argument_list|(
literal|"payData is null and it shouldn't be"
argument_list|,
name|payData
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"payData is null and it shouldn't be"
argument_list|,
name|payData
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|float
name|payFloat
init|=
name|PayloadHelper
operator|.
name|decodeFloat
argument_list|(
name|payData
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|payFloat
operator|+
literal|" does not equal: "
operator|+
literal|0.1f
argument_list|,
name|payFloat
operator|==
literal|0.1f
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testDelim
specifier|public
name|void
name|testDelim
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
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
name|args
operator|.
name|put
argument_list|(
name|DelimitedPayloadTokenFilterFactory
operator|.
name|ENCODER_ATTR
argument_list|,
name|FloatEncoder
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|DelimitedPayloadTokenFilterFactory
operator|.
name|DELIMITER_ATTR
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|DelimitedPayloadTokenFilterFactory
name|factory
init|=
operator|new
name|DelimitedPayloadTokenFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|ResourceLoader
name|loader
init|=
operator|new
name|SolrResourceLoader
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|factory
operator|.
name|inform
argument_list|(
name|loader
argument_list|)
expr_stmt|;
name|TokenStream
name|input
init|=
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"the*0.1 quick*0.1 red*0.1"
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|DelimitedPayloadTokenFilter
name|tf
init|=
name|factory
operator|.
name|create
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|tf
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|tf
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|PayloadAttribute
name|payAttr
init|=
name|tf
operator|.
name|getAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"payAttr is null and it shouldn't be"
argument_list|,
name|payAttr
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|byte
index|[]
name|payData
init|=
name|payAttr
operator|.
name|getPayload
argument_list|()
operator|.
name|bytes
decl_stmt|;
name|assertTrue
argument_list|(
literal|"payData is null and it shouldn't be"
argument_list|,
name|payData
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|float
name|payFloat
init|=
name|PayloadHelper
operator|.
name|decodeFloat
argument_list|(
name|payData
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|payFloat
operator|+
literal|" does not equal: "
operator|+
literal|0.1f
argument_list|,
name|payFloat
operator|==
literal|0.1f
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
