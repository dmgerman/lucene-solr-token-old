begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.ja.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ja
operator|.
name|util
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
name|FileInputStream
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
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|LineNumberReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|CharsetDecoder
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|CodingErrorAction
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import
begin_class
DECL|class|ConnectionCostsBuilder
specifier|public
class|class
name|ConnectionCostsBuilder
block|{
DECL|method|ConnectionCostsBuilder
specifier|private
name|ConnectionCostsBuilder
parameter_list|()
block|{   }
DECL|method|build
specifier|public
specifier|static
name|ConnectionCostsWriter
name|build
parameter_list|(
name|String
name|filename
parameter_list|)
throws|throws
name|IOException
block|{
name|FileInputStream
name|inputStream
init|=
operator|new
name|FileInputStream
argument_list|(
name|filename
argument_list|)
decl_stmt|;
name|Charset
name|cs
init|=
name|StandardCharsets
operator|.
name|US_ASCII
decl_stmt|;
name|CharsetDecoder
name|decoder
init|=
name|cs
operator|.
name|newDecoder
argument_list|()
operator|.
name|onMalformedInput
argument_list|(
name|CodingErrorAction
operator|.
name|REPORT
argument_list|)
operator|.
name|onUnmappableCharacter
argument_list|(
name|CodingErrorAction
operator|.
name|REPORT
argument_list|)
decl_stmt|;
name|InputStreamReader
name|streamReader
init|=
operator|new
name|InputStreamReader
argument_list|(
name|inputStream
argument_list|,
name|decoder
argument_list|)
decl_stmt|;
name|LineNumberReader
name|lineReader
init|=
operator|new
name|LineNumberReader
argument_list|(
name|streamReader
argument_list|)
decl_stmt|;
name|String
name|line
init|=
name|lineReader
operator|.
name|readLine
argument_list|()
decl_stmt|;
name|String
index|[]
name|dimensions
init|=
name|line
operator|.
name|split
argument_list|(
literal|"\\s+"
argument_list|)
decl_stmt|;
assert|assert
name|dimensions
operator|.
name|length
operator|==
literal|2
assert|;
name|int
name|forwardSize
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|dimensions
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|int
name|backwardSize
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|dimensions
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
assert|assert
name|forwardSize
operator|>
literal|0
operator|&&
name|backwardSize
operator|>
literal|0
assert|;
name|ConnectionCostsWriter
name|costs
init|=
operator|new
name|ConnectionCostsWriter
argument_list|(
name|forwardSize
argument_list|,
name|backwardSize
argument_list|)
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|lineReader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|fields
init|=
name|line
operator|.
name|split
argument_list|(
literal|"\\s+"
argument_list|)
decl_stmt|;
assert|assert
name|fields
operator|.
name|length
operator|==
literal|3
assert|;
name|int
name|forwardId
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|fields
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|int
name|backwardId
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|fields
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|int
name|cost
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|fields
index|[
literal|2
index|]
argument_list|)
decl_stmt|;
name|costs
operator|.
name|add
argument_list|(
name|forwardId
argument_list|,
name|backwardId
argument_list|,
name|cost
argument_list|)
expr_stmt|;
block|}
return|return
name|costs
return|;
block|}
block|}
end_class
end_unit
