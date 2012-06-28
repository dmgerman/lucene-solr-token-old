begin_unit
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|CharTermAttributeImpl
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
begin_comment
comment|/**  * Extension of {@link CharTermAttributeImpl} that encodes the term  * text as UTF-16 bytes instead of as UTF-8 bytes.  */
end_comment
begin_class
DECL|class|MockUTF16TermAttributeImpl
specifier|public
class|class
name|MockUTF16TermAttributeImpl
extends|extends
name|CharTermAttributeImpl
block|{
DECL|field|charset
specifier|static
specifier|final
name|Charset
name|charset
init|=
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-16LE"
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|fillBytesRef
specifier|public
name|int
name|fillBytesRef
parameter_list|()
block|{
name|BytesRef
name|bytes
init|=
name|getBytesRef
argument_list|()
decl_stmt|;
name|byte
index|[]
name|utf16
init|=
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|(
name|charset
argument_list|)
decl_stmt|;
name|bytes
operator|.
name|bytes
operator|=
name|utf16
expr_stmt|;
name|bytes
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|bytes
operator|.
name|length
operator|=
name|utf16
operator|.
name|length
expr_stmt|;
return|return
name|bytes
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class
end_unit
