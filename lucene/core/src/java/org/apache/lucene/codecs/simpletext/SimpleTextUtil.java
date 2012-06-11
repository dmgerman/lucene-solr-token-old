begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.simpletext
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|simpletext
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|DataInput
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
name|store
operator|.
name|DataOutput
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
name|util
operator|.
name|BytesRef
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
name|util
operator|.
name|UnicodeUtil
import|;
end_import
begin_class
DECL|class|SimpleTextUtil
class|class
name|SimpleTextUtil
block|{
DECL|field|NEWLINE
specifier|public
specifier|final
specifier|static
name|byte
name|NEWLINE
init|=
literal|10
decl_stmt|;
DECL|field|ESCAPE
specifier|public
specifier|final
specifier|static
name|byte
name|ESCAPE
init|=
literal|92
decl_stmt|;
DECL|method|write
specifier|public
specifier|static
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|,
name|String
name|s
parameter_list|,
name|BytesRef
name|scratch
parameter_list|)
throws|throws
name|IOException
block|{
name|UnicodeUtil
operator|.
name|UTF16toUTF8
argument_list|(
name|s
argument_list|,
literal|0
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|out
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
block|}
DECL|method|write
specifier|public
specifier|static
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|,
name|BytesRef
name|b
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|b
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|byte
name|bx
init|=
name|b
operator|.
name|bytes
index|[
name|b
operator|.
name|offset
operator|+
name|i
index|]
decl_stmt|;
if|if
condition|(
name|bx
operator|==
name|NEWLINE
operator|||
name|bx
operator|==
name|ESCAPE
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|ESCAPE
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeByte
argument_list|(
name|bx
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeNewline
specifier|public
specifier|static
name|void
name|writeNewline
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|NEWLINE
argument_list|)
expr_stmt|;
block|}
DECL|method|readLine
specifier|public
specifier|static
name|void
name|readLine
parameter_list|(
name|DataInput
name|in
parameter_list|,
name|BytesRef
name|scratch
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|upto
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|byte
name|b
init|=
name|in
operator|.
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
name|scratch
operator|.
name|bytes
operator|.
name|length
operator|==
name|upto
condition|)
block|{
name|scratch
operator|.
name|grow
argument_list|(
literal|1
operator|+
name|upto
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|b
operator|==
name|ESCAPE
condition|)
block|{
name|scratch
operator|.
name|bytes
index|[
name|upto
operator|++
index|]
operator|=
name|in
operator|.
name|readByte
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|b
operator|==
name|NEWLINE
condition|)
block|{
break|break;
block|}
else|else
block|{
name|scratch
operator|.
name|bytes
index|[
name|upto
operator|++
index|]
operator|=
name|b
expr_stmt|;
block|}
block|}
block|}
name|scratch
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|scratch
operator|.
name|length
operator|=
name|upto
expr_stmt|;
block|}
block|}
end_class
end_unit
