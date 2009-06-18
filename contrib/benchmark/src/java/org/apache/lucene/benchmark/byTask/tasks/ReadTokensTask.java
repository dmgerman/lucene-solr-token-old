begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.tasks
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|tasks
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
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|Analyzer
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
name|Token
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
name|benchmark
operator|.
name|byTask
operator|.
name|PerfRunData
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
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
operator|.
name|DocMaker
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
import|;
end_import
begin_comment
comment|/**  * Simple task to test performance of tokenizers.  It just  * creates a token stream for each field of the document and  * read all tokens out of that stream.  */
end_comment
begin_class
DECL|class|ReadTokensTask
specifier|public
class|class
name|ReadTokensTask
extends|extends
name|PerfTask
block|{
DECL|method|ReadTokensTask
specifier|public
name|ReadTokensTask
parameter_list|(
name|PerfRunData
name|runData
parameter_list|)
block|{
name|super
argument_list|(
name|runData
argument_list|)
expr_stmt|;
block|}
DECL|field|totalTokenCount
specifier|private
name|int
name|totalTokenCount
init|=
literal|0
decl_stmt|;
comment|// volatile data passed between setup(), doLogic(), tearDown().
DECL|field|doc
specifier|private
name|Document
name|doc
init|=
literal|null
decl_stmt|;
DECL|method|setup
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
name|DocMaker
name|docMaker
init|=
name|getRunData
argument_list|()
operator|.
name|getDocMaker
argument_list|()
decl_stmt|;
name|doc
operator|=
name|docMaker
operator|.
name|makeDocument
argument_list|()
expr_stmt|;
block|}
DECL|method|getLogMessage
specifier|protected
name|String
name|getLogMessage
parameter_list|(
name|int
name|recsCount
parameter_list|)
block|{
return|return
literal|"read "
operator|+
name|recsCount
operator|+
literal|" docs; "
operator|+
name|totalTokenCount
operator|+
literal|" tokens"
return|;
block|}
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|doc
operator|=
literal|null
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|field|token
name|Token
name|token
init|=
operator|new
name|Token
argument_list|()
decl_stmt|;
DECL|method|doLogic
specifier|public
name|int
name|doLogic
parameter_list|()
throws|throws
name|Exception
block|{
name|List
name|fields
init|=
name|doc
operator|.
name|getFields
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numField
init|=
name|fields
operator|.
name|size
argument_list|()
decl_stmt|;
name|Analyzer
name|analyzer
init|=
name|getRunData
argument_list|()
operator|.
name|getAnalyzer
argument_list|()
decl_stmt|;
name|int
name|tokenCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numField
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Field
name|field
init|=
operator|(
name|Field
operator|)
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|TokenStream
name|stream
decl_stmt|;
specifier|final
name|TokenStream
name|streamValue
init|=
name|field
operator|.
name|tokenStreamValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|streamValue
operator|!=
literal|null
condition|)
name|stream
operator|=
name|streamValue
expr_stmt|;
else|else
block|{
comment|// the field does not have a TokenStream,
comment|// so we have to obtain one from the analyzer
specifier|final
name|Reader
name|reader
decl_stmt|;
comment|// find or make Reader
specifier|final
name|Reader
name|readerValue
init|=
name|field
operator|.
name|readerValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|readerValue
operator|!=
literal|null
condition|)
name|reader
operator|=
name|readerValue
expr_stmt|;
else|else
block|{
name|String
name|stringValue
init|=
name|field
operator|.
name|stringValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|stringValue
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field must have either TokenStream, String or Reader value"
argument_list|)
throw|;
name|stringReader
operator|.
name|init
argument_list|(
name|stringValue
argument_list|)
expr_stmt|;
name|reader
operator|=
name|stringReader
expr_stmt|;
block|}
comment|// Tokenize field
name|stream
operator|=
name|analyzer
operator|.
name|reusableTokenStream
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|,
name|reader
argument_list|)
expr_stmt|;
block|}
comment|// reset the TokenStream to the first token
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|stream
operator|.
name|next
argument_list|(
name|token
argument_list|)
operator|!=
literal|null
condition|)
name|tokenCount
operator|++
expr_stmt|;
block|}
name|totalTokenCount
operator|+=
name|tokenCount
expr_stmt|;
return|return
name|tokenCount
return|;
block|}
comment|/* Simple StringReader that can be reset to a new string;    * we use this when tokenizing the string value from a    * Field. */
DECL|field|stringReader
name|ReusableStringReader
name|stringReader
init|=
operator|new
name|ReusableStringReader
argument_list|()
decl_stmt|;
DECL|class|ReusableStringReader
specifier|private
specifier|final
specifier|static
class|class
name|ReusableStringReader
extends|extends
name|Reader
block|{
DECL|field|upto
name|int
name|upto
decl_stmt|;
DECL|field|left
name|int
name|left
decl_stmt|;
DECL|field|s
name|String
name|s
decl_stmt|;
DECL|method|init
name|void
name|init
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|this
operator|.
name|s
operator|=
name|s
expr_stmt|;
name|left
operator|=
name|s
operator|.
name|length
argument_list|()
expr_stmt|;
name|this
operator|.
name|upto
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
name|char
index|[]
name|c
parameter_list|)
block|{
return|return
name|read
argument_list|(
name|c
argument_list|,
literal|0
argument_list|,
name|c
operator|.
name|length
argument_list|)
return|;
block|}
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
name|char
index|[]
name|c
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
if|if
condition|(
name|left
operator|>
name|len
condition|)
block|{
name|s
operator|.
name|getChars
argument_list|(
name|upto
argument_list|,
name|upto
operator|+
name|len
argument_list|,
name|c
argument_list|,
name|off
argument_list|)
expr_stmt|;
name|upto
operator|+=
name|len
expr_stmt|;
name|left
operator|-=
name|len
expr_stmt|;
return|return
name|len
return|;
block|}
elseif|else
if|if
condition|(
literal|0
operator|==
name|left
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
name|s
operator|.
name|getChars
argument_list|(
name|upto
argument_list|,
name|upto
operator|+
name|left
argument_list|,
name|c
argument_list|,
name|off
argument_list|)
expr_stmt|;
name|int
name|r
init|=
name|left
decl_stmt|;
name|left
operator|=
literal|0
expr_stmt|;
name|upto
operator|=
name|s
operator|.
name|length
argument_list|()
expr_stmt|;
return|return
name|r
return|;
block|}
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{}
empty_stmt|;
block|}
block|}
end_class
end_unit
