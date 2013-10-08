begin_unit
begin_package
DECL|package|org.apache.lucene.search.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
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
name|Arrays
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
name|List
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
name|index
operator|.
name|IndexReader
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
name|MultiFields
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
name|StorableField
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
name|StoredDocument
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
name|search
operator|.
name|spell
operator|.
name|Dictionary
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
name|search
operator|.
name|spell
operator|.
name|TermFreqPayloadIterator
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
name|search
operator|.
name|suggest
operator|.
name|analyzing
operator|.
name|AnalyzingInfixSuggester
import|;
end_import
begin_comment
comment|// javadoc
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|fst
operator|.
name|FSTCompletionLookup
import|;
end_import
begin_comment
comment|// javadoc
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|fst
operator|.
name|WFSTCompletionLookup
import|;
end_import
begin_comment
comment|// javadoc
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|jaspell
operator|.
name|JaspellLookup
import|;
end_import
begin_comment
comment|// javadoc
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|tst
operator|.
name|TSTLookup
import|;
end_import
begin_comment
comment|// javadoc
end_comment
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
name|Bits
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
name|BytesRefIterator
import|;
end_import
begin_comment
comment|/**  * Dictionary with terms, weights and optionally payload information   * taken from stored fields in a Lucene index.  *   *<b>NOTE:</b>   *<ul>  *<li>  *      The term, weight and (optionally) payload fields supplied  *      are required for ALL documents and has to be stored  *</li>  *<li>  *      This Dictionary implementation is not compatible with the following Suggesters:   *      {@link JaspellLookup}, {@link TSTLookup}, {@link FSTCompletionLookup},  *      {@link WFSTCompletionLookup} and {@link AnalyzingInfixSuggester}.   *      see https://issues.apache.org/jira/browse/LUCENE-5260  *</li>  *</ul>  */
end_comment
begin_class
DECL|class|DocumentDictionary
specifier|public
class|class
name|DocumentDictionary
implements|implements
name|Dictionary
block|{
DECL|field|reader
specifier|private
specifier|final
name|IndexReader
name|reader
decl_stmt|;
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|weightField
specifier|private
specifier|final
name|String
name|weightField
decl_stmt|;
DECL|field|payloadField
specifier|private
specifier|final
name|String
name|payloadField
decl_stmt|;
comment|/**    * Creates a new dictionary with the contents of the fields named<code>field</code>    * for the terms and<code>weightField</code> for the weights that will be used for    * the corresponding terms.    */
DECL|method|DocumentDictionary
specifier|public
name|DocumentDictionary
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|weightField
parameter_list|)
block|{
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|weightField
operator|=
name|weightField
expr_stmt|;
name|this
operator|.
name|payloadField
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Creates a new dictionary with the contents of the fields named<code>field</code>    * for the terms,<code>weightField</code> for the weights that will be used for the     * the corresponding terms and<code>payloadField</code> for the corresponding payloads    * for the entry.    */
DECL|method|DocumentDictionary
specifier|public
name|DocumentDictionary
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|weightField
parameter_list|,
name|String
name|payloadField
parameter_list|)
block|{
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|weightField
operator|=
name|weightField
expr_stmt|;
name|this
operator|.
name|payloadField
operator|=
name|payloadField
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getWordsIterator
specifier|public
name|BytesRefIterator
name|getWordsIterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|TermWeightPayloadIterator
argument_list|(
name|payloadField
operator|!=
literal|null
argument_list|)
return|;
block|}
DECL|class|TermWeightPayloadIterator
specifier|final
class|class
name|TermWeightPayloadIterator
implements|implements
name|TermFreqPayloadIterator
block|{
DECL|field|docCount
specifier|private
specifier|final
name|int
name|docCount
decl_stmt|;
DECL|field|relevantFields
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|relevantFields
decl_stmt|;
DECL|field|withPayload
specifier|private
specifier|final
name|boolean
name|withPayload
decl_stmt|;
DECL|field|liveDocs
specifier|private
specifier|final
name|Bits
name|liveDocs
decl_stmt|;
DECL|field|currentDocId
specifier|private
name|int
name|currentDocId
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|currentWeight
specifier|private
name|long
name|currentWeight
decl_stmt|;
DECL|field|currentPayload
specifier|private
name|BytesRef
name|currentPayload
decl_stmt|;
comment|/**      * Creates an iterator over term, weight and payload fields from the lucene      * index. setting<code>withPayload</code> to false, implies an iterator      * over only term and weight.      */
DECL|method|TermWeightPayloadIterator
specifier|public
name|TermWeightPayloadIterator
parameter_list|(
name|boolean
name|withPayload
parameter_list|)
throws|throws
name|IOException
block|{
name|docCount
operator|=
name|reader
operator|.
name|maxDoc
argument_list|()
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|withPayload
operator|=
name|withPayload
expr_stmt|;
name|currentPayload
operator|=
literal|null
expr_stmt|;
name|liveDocs
operator|=
name|MultiFields
operator|.
name|getLiveDocs
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|relevantFieldList
decl_stmt|;
if|if
condition|(
name|withPayload
condition|)
block|{
name|relevantFieldList
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|field
argument_list|,
name|weightField
argument_list|,
name|payloadField
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|relevantFieldList
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|field
argument_list|,
name|weightField
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|relevantFields
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|relevantFieldList
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|weight
specifier|public
name|long
name|weight
parameter_list|()
block|{
return|return
name|currentWeight
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
name|currentDocId
operator|<
name|docCount
condition|)
block|{
name|currentDocId
operator|++
expr_stmt|;
if|if
condition|(
name|liveDocs
operator|!=
literal|null
operator|&&
operator|!
name|liveDocs
operator|.
name|get
argument_list|(
name|currentDocId
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|StoredDocument
name|doc
init|=
name|reader
operator|.
name|document
argument_list|(
name|currentDocId
argument_list|,
name|relevantFields
argument_list|)
decl_stmt|;
if|if
condition|(
name|withPayload
condition|)
block|{
name|StorableField
name|payload
init|=
name|doc
operator|.
name|getField
argument_list|(
name|payloadField
argument_list|)
decl_stmt|;
if|if
condition|(
name|payload
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|payloadField
operator|+
literal|" does not exist"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|payload
operator|.
name|binaryValue
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|payloadField
operator|+
literal|" does not have binary value"
argument_list|)
throw|;
block|}
name|currentPayload
operator|=
name|payload
operator|.
name|binaryValue
argument_list|()
expr_stmt|;
block|}
name|StorableField
name|weight
init|=
name|doc
operator|.
name|getField
argument_list|(
name|weightField
argument_list|)
decl_stmt|;
if|if
condition|(
name|weight
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|weightField
operator|+
literal|" does not exist"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|weight
operator|.
name|numericValue
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|weightField
operator|+
literal|" does not have numeric value"
argument_list|)
throw|;
block|}
name|currentWeight
operator|=
name|weight
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
expr_stmt|;
name|StorableField
name|fieldVal
init|=
name|doc
operator|.
name|getField
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldVal
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|field
operator|+
literal|" does not exist"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|fieldVal
operator|.
name|stringValue
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|field
operator|+
literal|" does not have string value"
argument_list|)
throw|;
block|}
return|return
operator|new
name|BytesRef
argument_list|(
name|fieldVal
operator|.
name|stringValue
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|payload
specifier|public
name|BytesRef
name|payload
parameter_list|()
block|{
return|return
name|currentPayload
return|;
block|}
block|}
block|}
end_class
end_unit
