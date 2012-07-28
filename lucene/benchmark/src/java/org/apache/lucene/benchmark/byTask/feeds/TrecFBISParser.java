begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds
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
name|feeds
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|util
operator|.
name|Date
import|;
end_import
begin_comment
comment|/**  * Parser for the FBIS docs in trec disks 4+5 collection format  */
end_comment
begin_class
DECL|class|TrecFBISParser
specifier|public
class|class
name|TrecFBISParser
extends|extends
name|TrecDocParser
block|{
DECL|field|HEADER
specifier|private
specifier|static
specifier|final
name|String
name|HEADER
init|=
literal|"<HEADER>"
decl_stmt|;
DECL|field|HEADER_END
specifier|private
specifier|static
specifier|final
name|String
name|HEADER_END
init|=
literal|"</HEADER>"
decl_stmt|;
DECL|field|HEADER_END_LENGTH
specifier|private
specifier|static
specifier|final
name|int
name|HEADER_END_LENGTH
init|=
name|HEADER_END
operator|.
name|length
argument_list|()
decl_stmt|;
DECL|field|DATE1
specifier|private
specifier|static
specifier|final
name|String
name|DATE1
init|=
literal|"<DATE1>"
decl_stmt|;
DECL|field|DATE1_END
specifier|private
specifier|static
specifier|final
name|String
name|DATE1_END
init|=
literal|"</DATE1>"
decl_stmt|;
DECL|field|TI
specifier|private
specifier|static
specifier|final
name|String
name|TI
init|=
literal|"<TI>"
decl_stmt|;
DECL|field|TI_END
specifier|private
specifier|static
specifier|final
name|String
name|TI_END
init|=
literal|"</TI>"
decl_stmt|;
annotation|@
name|Override
DECL|method|parse
specifier|public
name|DocData
name|parse
parameter_list|(
name|DocData
name|docData
parameter_list|,
name|String
name|name
parameter_list|,
name|TrecContentSource
name|trecSrc
parameter_list|,
name|StringBuilder
name|docBuf
parameter_list|,
name|ParsePathType
name|pathType
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|mark
init|=
literal|0
decl_stmt|;
comment|// that much is skipped
comment|// optionally skip some of the text, set date, title
name|Date
name|date
init|=
literal|null
decl_stmt|;
name|String
name|title
init|=
literal|null
decl_stmt|;
name|int
name|h1
init|=
name|docBuf
operator|.
name|indexOf
argument_list|(
name|HEADER
argument_list|)
decl_stmt|;
if|if
condition|(
name|h1
operator|>=
literal|0
condition|)
block|{
name|int
name|h2
init|=
name|docBuf
operator|.
name|indexOf
argument_list|(
name|HEADER_END
argument_list|,
name|h1
argument_list|)
decl_stmt|;
name|mark
operator|=
name|h2
operator|+
name|HEADER_END_LENGTH
expr_stmt|;
comment|// date...
name|String
name|dateStr
init|=
name|extract
argument_list|(
name|docBuf
argument_list|,
name|DATE1
argument_list|,
name|DATE1_END
argument_list|,
name|h2
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|dateStr
operator|!=
literal|null
condition|)
block|{
name|date
operator|=
name|trecSrc
operator|.
name|parseDate
argument_list|(
name|dateStr
argument_list|)
expr_stmt|;
block|}
comment|// title...
name|title
operator|=
name|extract
argument_list|(
name|docBuf
argument_list|,
name|TI
argument_list|,
name|TI_END
argument_list|,
name|h2
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|docData
operator|.
name|clear
argument_list|()
expr_stmt|;
name|docData
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|docData
operator|.
name|setDate
argument_list|(
name|date
argument_list|)
expr_stmt|;
name|docData
operator|.
name|setTitle
argument_list|(
name|title
argument_list|)
expr_stmt|;
name|docData
operator|.
name|setBody
argument_list|(
name|stripTags
argument_list|(
name|docBuf
argument_list|,
name|mark
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|docData
return|;
block|}
block|}
end_class
end_unit
