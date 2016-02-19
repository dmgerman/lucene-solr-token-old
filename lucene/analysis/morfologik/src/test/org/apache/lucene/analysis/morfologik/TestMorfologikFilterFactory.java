begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.morfologik
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|morfologik
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import
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
name|Collections
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
name|util
operator|.
name|ClasspathResourceLoader
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
begin_comment
comment|/**  * Test for {@link MorfologikFilterFactory}.  */
end_comment
begin_class
DECL|class|TestMorfologikFilterFactory
specifier|public
class|class
name|TestMorfologikFilterFactory
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|class|ForbidResourcesLoader
specifier|private
specifier|static
class|class
name|ForbidResourcesLoader
implements|implements
name|ResourceLoader
block|{
annotation|@
name|Override
DECL|method|openResource
specifier|public
name|InputStream
name|openResource
parameter_list|(
name|String
name|resource
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|findClass
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|findClass
parameter_list|(
name|String
name|cname
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|expectedType
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|newInstance
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|newInstance
parameter_list|(
name|String
name|cname
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|expectedType
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
DECL|method|testDefaultDictionary
specifier|public
name|void
name|testDefaultDictionary
parameter_list|()
throws|throws
name|Exception
block|{
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"rowery bilety"
argument_list|)
decl_stmt|;
name|MorfologikFilterFactory
name|factory
init|=
operator|new
name|MorfologikFilterFactory
argument_list|(
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|String
operator|>
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
name|factory
operator|.
name|inform
argument_list|(
operator|new
name|ForbidResourcesLoader
argument_list|()
argument_list|)
expr_stmt|;
name|TokenStream
name|stream
init|=
name|whitespaceMockTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|stream
operator|=
name|factory
operator|.
name|create
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"rower"
block|,
literal|"bilet"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testExplicitDictionary
specifier|public
name|void
name|testExplicitDictionary
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|ResourceLoader
name|loader
init|=
operator|new
name|ClasspathResourceLoader
argument_list|(
name|TestMorfologikFilterFactory
operator|.
name|class
argument_list|)
decl_stmt|;
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"inflected1 inflected2"
argument_list|)
decl_stmt|;
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
argument_list|<>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
name|MorfologikFilterFactory
operator|.
name|DICTIONARY_ATTRIBUTE
argument_list|,
literal|"custom-dictionary.dict"
argument_list|)
expr_stmt|;
name|MorfologikFilterFactory
name|factory
init|=
operator|new
name|MorfologikFilterFactory
argument_list|(
name|params
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
name|stream
init|=
name|whitespaceMockTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|stream
operator|=
name|factory
operator|.
name|create
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"lemma1"
block|,
literal|"lemma2"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testMissingDictionary
specifier|public
name|void
name|testMissingDictionary
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|ResourceLoader
name|loader
init|=
operator|new
name|ClasspathResourceLoader
argument_list|(
name|TestMorfologikFilterFactory
operator|.
name|class
argument_list|)
decl_stmt|;
name|IOException
name|expected
init|=
name|expectThrows
argument_list|(
name|IOException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
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
argument_list|<>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
name|MorfologikFilterFactory
operator|.
name|DICTIONARY_ATTRIBUTE
argument_list|,
literal|"missing-dictionary-resource.dict"
argument_list|)
expr_stmt|;
name|MorfologikFilterFactory
name|factory
init|=
operator|new
name|MorfologikFilterFactory
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|factory
operator|.
name|inform
argument_list|(
name|loader
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Resource not found"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test that bogus arguments result in exception */
DECL|method|testBogusArguments
specifier|public
name|void
name|testBogusArguments
parameter_list|()
throws|throws
name|Exception
block|{
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|HashMap
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
literal|"bogusArg"
argument_list|,
literal|"bogusValue"
argument_list|)
expr_stmt|;
operator|new
name|MorfologikFilterFactory
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Unknown parameters"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
