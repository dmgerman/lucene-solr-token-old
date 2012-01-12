begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.kuromoji.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|kuromoji
operator|.
name|util
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_class
DECL|class|DictionaryBuilder
specifier|public
class|class
name|DictionaryBuilder
block|{
DECL|enum|DictionaryFormat
DECL|enum constant|IPADIC
DECL|enum constant|UNIDIC
specifier|public
enum|enum
name|DictionaryFormat
block|{
name|IPADIC
block|,
name|UNIDIC
block|}
empty_stmt|;
DECL|method|DictionaryBuilder
specifier|private
name|DictionaryBuilder
parameter_list|()
block|{   }
DECL|method|build
specifier|public
specifier|static
name|void
name|build
parameter_list|(
name|DictionaryFormat
name|format
parameter_list|,
name|String
name|inputDirname
parameter_list|,
name|String
name|outputDirname
parameter_list|,
name|String
name|encoding
parameter_list|,
name|boolean
name|normalizeEntry
parameter_list|)
throws|throws
name|IOException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"building tokeninfo dict..."
argument_list|)
expr_stmt|;
name|TokenInfoDictionaryBuilder
name|tokenInfoBuilder
init|=
operator|new
name|TokenInfoDictionaryBuilder
argument_list|(
name|format
argument_list|,
name|encoding
argument_list|,
name|normalizeEntry
argument_list|)
decl_stmt|;
name|TokenInfoDictionaryWriter
name|tokenInfoDictionary
init|=
name|tokenInfoBuilder
operator|.
name|build
argument_list|(
name|inputDirname
argument_list|)
decl_stmt|;
name|tokenInfoDictionary
operator|.
name|write
argument_list|(
name|outputDirname
argument_list|)
expr_stmt|;
name|tokenInfoDictionary
operator|=
literal|null
expr_stmt|;
name|tokenInfoBuilder
operator|=
literal|null
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"done"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"building unknown word dict..."
argument_list|)
expr_stmt|;
name|UnknownDictionaryBuilder
name|unkBuilder
init|=
operator|new
name|UnknownDictionaryBuilder
argument_list|(
name|encoding
argument_list|)
decl_stmt|;
name|UnknownDictionaryWriter
name|unkDictionary
init|=
name|unkBuilder
operator|.
name|build
argument_list|(
name|inputDirname
argument_list|)
decl_stmt|;
name|unkDictionary
operator|.
name|write
argument_list|(
name|outputDirname
argument_list|)
expr_stmt|;
name|unkDictionary
operator|=
literal|null
expr_stmt|;
name|unkBuilder
operator|=
literal|null
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"done"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"building connection costs..."
argument_list|)
expr_stmt|;
name|ConnectionCostsWriter
name|connectionCosts
init|=
name|ConnectionCostsBuilder
operator|.
name|build
argument_list|(
name|inputDirname
operator|+
name|File
operator|.
name|separator
operator|+
literal|"matrix.def"
argument_list|)
decl_stmt|;
name|connectionCosts
operator|.
name|write
argument_list|(
name|outputDirname
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"done"
argument_list|)
expr_stmt|;
block|}
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
block|{
name|DictionaryFormat
name|format
decl_stmt|;
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"ipadic"
argument_list|)
condition|)
block|{
name|format
operator|=
name|DictionaryFormat
operator|.
name|IPADIC
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"unidic"
argument_list|)
condition|)
block|{
name|format
operator|=
name|DictionaryFormat
operator|.
name|UNIDIC
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Illegal format "
operator|+
name|args
index|[
literal|0
index|]
operator|+
literal|" using unidic instead"
argument_list|)
expr_stmt|;
name|format
operator|=
name|DictionaryFormat
operator|.
name|IPADIC
expr_stmt|;
block|}
name|String
name|inputDirname
init|=
name|args
index|[
literal|1
index|]
decl_stmt|;
name|String
name|outputDirname
init|=
name|args
index|[
literal|2
index|]
decl_stmt|;
name|String
name|inputEncoding
init|=
name|args
index|[
literal|3
index|]
decl_stmt|;
name|boolean
name|normalizeEntries
init|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|args
index|[
literal|4
index|]
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"dictionary builder"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"dictionary format: "
operator|+
name|format
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"input directory: "
operator|+
name|inputDirname
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"output directory: "
operator|+
name|outputDirname
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"input encoding: "
operator|+
name|inputEncoding
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"normalize entries: "
operator|+
name|normalizeEntries
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|DictionaryBuilder
operator|.
name|build
argument_list|(
name|format
argument_list|,
name|inputDirname
argument_list|,
name|outputDirname
argument_list|,
name|inputEncoding
argument_list|,
name|normalizeEntries
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
