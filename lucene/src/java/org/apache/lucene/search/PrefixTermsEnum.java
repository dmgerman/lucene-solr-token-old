begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|index
operator|.
name|FilteredTermsEnum
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
name|index
operator|.
name|TermsEnum
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
comment|/**  * Subclass of FilteredTermEnum for enumerating all terms that match the  * specified prefix filter term.  *<p>Term enumerations are always ordered by  * {@link #getComparator}.  Each term in the enumeration is  * greater than all that precede it.</p>  */
end_comment
begin_class
DECL|class|PrefixTermsEnum
specifier|public
class|class
name|PrefixTermsEnum
extends|extends
name|FilteredTermsEnum
block|{
DECL|field|prefixRef
specifier|private
specifier|final
name|BytesRef
name|prefixRef
decl_stmt|;
DECL|method|PrefixTermsEnum
specifier|public
name|PrefixTermsEnum
parameter_list|(
name|TermsEnum
name|tenum
parameter_list|,
name|BytesRef
name|prefixText
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|tenum
argument_list|)
expr_stmt|;
name|setInitialSeekTerm
argument_list|(
name|this
operator|.
name|prefixRef
operator|=
name|prefixText
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|accept
specifier|protected
name|AcceptStatus
name|accept
parameter_list|(
name|BytesRef
name|term
parameter_list|)
block|{
if|if
condition|(
name|term
operator|.
name|startsWith
argument_list|(
name|prefixRef
argument_list|)
condition|)
block|{
return|return
name|AcceptStatus
operator|.
name|YES
return|;
block|}
else|else
block|{
return|return
name|AcceptStatus
operator|.
name|END
return|;
block|}
block|}
block|}
end_class
end_unit
