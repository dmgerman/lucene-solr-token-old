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
name|AbstractMap
operator|.
name|SimpleEntry
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|analysis
operator|.
name|MockAnalyzer
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
name|NumericDocValuesField
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
name|StoredField
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
name|TextField
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
name|DirectoryReader
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
name|IndexWriterConfig
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
name|RandomIndexWriter
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
name|Term
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
name|store
operator|.
name|Directory
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
name|LuceneTestCase
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|DocumentDictionaryTest
specifier|public
class|class
name|DocumentDictionaryTest
extends|extends
name|LuceneTestCase
block|{
DECL|field|FIELD_NAME
specifier|static
specifier|final
name|String
name|FIELD_NAME
init|=
literal|"f1"
decl_stmt|;
DECL|field|WEIGHT_FIELD_NAME
specifier|static
specifier|final
name|String
name|WEIGHT_FIELD_NAME
init|=
literal|"w1"
decl_stmt|;
DECL|field|PAYLOAD_FIELD_NAME
specifier|static
specifier|final
name|String
name|PAYLOAD_FIELD_NAME
init|=
literal|"p1"
decl_stmt|;
DECL|field|CONTEXT_FIELD_NAME
specifier|static
specifier|final
name|String
name|CONTEXT_FIELD_NAME
init|=
literal|"c1"
decl_stmt|;
comment|/** Returns Pair(list of invalid document terms, Map of document term -&gt; document) */
DECL|method|generateIndexDocuments
specifier|private
name|Map
operator|.
name|Entry
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Document
argument_list|>
argument_list|>
name|generateIndexDocuments
parameter_list|(
name|int
name|ndocs
parameter_list|,
name|boolean
name|requiresPayload
parameter_list|,
name|boolean
name|requiresContexts
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Document
argument_list|>
name|docs
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|invalidDocTerms
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
name|ndocs
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|boolean
name|invalidDoc
init|=
literal|false
decl_stmt|;
name|Field
name|field
init|=
literal|null
decl_stmt|;
comment|// usually have valid term field in document
if|if
condition|(
name|usually
argument_list|()
condition|)
block|{
name|field
operator|=
operator|new
name|TextField
argument_list|(
name|FIELD_NAME
argument_list|,
literal|"field_"
operator|+
name|i
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|invalidDoc
operator|=
literal|true
expr_stmt|;
block|}
comment|// even if payload is not required usually have it
if|if
condition|(
name|requiresPayload
operator|||
name|usually
argument_list|()
condition|)
block|{
comment|// usually have valid payload field in document
if|if
condition|(
name|usually
argument_list|()
condition|)
block|{
name|Field
name|payload
init|=
operator|new
name|StoredField
argument_list|(
name|PAYLOAD_FIELD_NAME
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"payload_"
operator|+
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|payload
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|requiresPayload
condition|)
block|{
name|invalidDoc
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|requiresContexts
operator|||
name|usually
argument_list|()
condition|)
block|{
if|if
condition|(
name|usually
argument_list|()
condition|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|atLeast
argument_list|(
literal|2
argument_list|)
condition|;
name|j
operator|++
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
name|CONTEXT_FIELD_NAME
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"context_"
operator|+
name|i
operator|+
literal|"_"
operator|+
name|j
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// we should allow entries without context
block|}
comment|// usually have valid weight field in document
if|if
condition|(
name|usually
argument_list|()
condition|)
block|{
name|Field
name|weight
init|=
operator|(
name|rarely
argument_list|()
operator|)
condition|?
operator|new
name|StoredField
argument_list|(
name|WEIGHT_FIELD_NAME
argument_list|,
literal|100d
operator|+
name|i
argument_list|)
else|:
operator|new
name|NumericDocValuesField
argument_list|(
name|WEIGHT_FIELD_NAME
argument_list|,
literal|100
operator|+
name|i
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|weight
argument_list|)
expr_stmt|;
block|}
name|String
name|term
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|invalidDoc
condition|)
block|{
name|term
operator|=
operator|(
name|field
operator|!=
literal|null
operator|)
condition|?
name|field
operator|.
name|stringValue
argument_list|()
else|:
literal|"invalid_"
operator|+
name|i
expr_stmt|;
name|invalidDocTerms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|term
operator|=
name|field
operator|.
name|stringValue
argument_list|()
expr_stmt|;
block|}
name|docs
operator|.
name|put
argument_list|(
name|term
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|SimpleEntry
argument_list|<>
argument_list|(
name|invalidDocTerms
argument_list|,
name|docs
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testEmptyReader
specifier|public
name|void
name|testEmptyReader
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
comment|// Make sure the index is created?
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|ir
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|Dictionary
name|dictionary
init|=
operator|new
name|DocumentDictionary
argument_list|(
name|ir
argument_list|,
name|FIELD_NAME
argument_list|,
name|WEIGHT_FIELD_NAME
argument_list|,
name|PAYLOAD_FIELD_NAME
argument_list|)
decl_stmt|;
name|InputIterator
name|inputIterator
init|=
name|dictionary
operator|.
name|getEntryIterator
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
name|inputIterator
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|inputIterator
operator|.
name|weight
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|inputIterator
operator|.
name|payload
argument_list|()
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|Map
operator|.
name|Entry
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Document
argument_list|>
argument_list|>
name|res
init|=
name|generateIndexDocuments
argument_list|(
name|atLeast
argument_list|(
literal|1000
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Document
argument_list|>
name|docs
init|=
name|res
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|invalidDocTerms
init|=
name|res
operator|.
name|getKey
argument_list|()
decl_stmt|;
for|for
control|(
name|Document
name|doc
range|:
name|docs
operator|.
name|values
argument_list|()
control|)
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|ir
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|Dictionary
name|dictionary
init|=
operator|new
name|DocumentDictionary
argument_list|(
name|ir
argument_list|,
name|FIELD_NAME
argument_list|,
name|WEIGHT_FIELD_NAME
argument_list|,
name|PAYLOAD_FIELD_NAME
argument_list|)
decl_stmt|;
name|InputIterator
name|inputIterator
init|=
name|dictionary
operator|.
name|getEntryIterator
argument_list|()
decl_stmt|;
name|BytesRef
name|f
decl_stmt|;
while|while
condition|(
operator|(
name|f
operator|=
name|inputIterator
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|Document
name|doc
init|=
name|docs
operator|.
name|remove
argument_list|(
name|f
operator|.
name|utf8ToString
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|equals
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|doc
operator|.
name|get
argument_list|(
name|FIELD_NAME
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Field
name|weightField
init|=
name|doc
operator|.
name|getField
argument_list|(
name|WEIGHT_FIELD_NAME
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|inputIterator
operator|.
name|weight
argument_list|()
argument_list|,
operator|(
name|weightField
operator|!=
literal|null
operator|)
condition|?
name|weightField
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
else|:
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|inputIterator
operator|.
name|payload
argument_list|()
operator|.
name|equals
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
name|PAYLOAD_FIELD_NAME
argument_list|)
operator|.
name|binaryValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|invalidTerm
range|:
name|invalidDocTerms
control|)
block|{
name|assertNotNull
argument_list|(
name|docs
operator|.
name|remove
argument_list|(
name|invalidTerm
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|docs
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWithoutPayload
specifier|public
name|void
name|testWithoutPayload
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|Map
operator|.
name|Entry
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Document
argument_list|>
argument_list|>
name|res
init|=
name|generateIndexDocuments
argument_list|(
name|atLeast
argument_list|(
literal|1000
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Document
argument_list|>
name|docs
init|=
name|res
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|invalidDocTerms
init|=
name|res
operator|.
name|getKey
argument_list|()
decl_stmt|;
for|for
control|(
name|Document
name|doc
range|:
name|docs
operator|.
name|values
argument_list|()
control|)
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|ir
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|Dictionary
name|dictionary
init|=
operator|new
name|DocumentDictionary
argument_list|(
name|ir
argument_list|,
name|FIELD_NAME
argument_list|,
name|WEIGHT_FIELD_NAME
argument_list|)
decl_stmt|;
name|InputIterator
name|inputIterator
init|=
name|dictionary
operator|.
name|getEntryIterator
argument_list|()
decl_stmt|;
name|BytesRef
name|f
decl_stmt|;
while|while
condition|(
operator|(
name|f
operator|=
name|inputIterator
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|Document
name|doc
init|=
name|docs
operator|.
name|remove
argument_list|(
name|f
operator|.
name|utf8ToString
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|equals
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|doc
operator|.
name|get
argument_list|(
name|FIELD_NAME
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Field
name|weightField
init|=
name|doc
operator|.
name|getField
argument_list|(
name|WEIGHT_FIELD_NAME
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|inputIterator
operator|.
name|weight
argument_list|()
argument_list|,
operator|(
name|weightField
operator|!=
literal|null
operator|)
condition|?
name|weightField
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
else|:
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|inputIterator
operator|.
name|payload
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|invalidTerm
range|:
name|invalidDocTerms
control|)
block|{
name|assertNotNull
argument_list|(
name|docs
operator|.
name|remove
argument_list|(
name|invalidTerm
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|docs
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWithContexts
specifier|public
name|void
name|testWithContexts
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|Map
operator|.
name|Entry
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Document
argument_list|>
argument_list|>
name|res
init|=
name|generateIndexDocuments
argument_list|(
name|atLeast
argument_list|(
literal|1000
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Document
argument_list|>
name|docs
init|=
name|res
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|invalidDocTerms
init|=
name|res
operator|.
name|getKey
argument_list|()
decl_stmt|;
for|for
control|(
name|Document
name|doc
range|:
name|docs
operator|.
name|values
argument_list|()
control|)
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|ir
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|Dictionary
name|dictionary
init|=
operator|new
name|DocumentDictionary
argument_list|(
name|ir
argument_list|,
name|FIELD_NAME
argument_list|,
name|WEIGHT_FIELD_NAME
argument_list|,
name|PAYLOAD_FIELD_NAME
argument_list|,
name|CONTEXT_FIELD_NAME
argument_list|)
decl_stmt|;
name|InputIterator
name|inputIterator
init|=
name|dictionary
operator|.
name|getEntryIterator
argument_list|()
decl_stmt|;
name|BytesRef
name|f
decl_stmt|;
while|while
condition|(
operator|(
name|f
operator|=
name|inputIterator
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|Document
name|doc
init|=
name|docs
operator|.
name|remove
argument_list|(
name|f
operator|.
name|utf8ToString
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|equals
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|doc
operator|.
name|get
argument_list|(
name|FIELD_NAME
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Field
name|weightField
init|=
name|doc
operator|.
name|getField
argument_list|(
name|WEIGHT_FIELD_NAME
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|inputIterator
operator|.
name|weight
argument_list|()
argument_list|,
operator|(
name|weightField
operator|!=
literal|null
operator|)
condition|?
name|weightField
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
else|:
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|inputIterator
operator|.
name|payload
argument_list|()
operator|.
name|equals
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
name|PAYLOAD_FIELD_NAME
argument_list|)
operator|.
name|binaryValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|oriCtxs
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|contextSet
init|=
name|inputIterator
operator|.
name|contexts
argument_list|()
decl_stmt|;
for|for
control|(
name|StorableField
name|ctxf
range|:
name|doc
operator|.
name|getFields
argument_list|(
name|CONTEXT_FIELD_NAME
argument_list|)
control|)
block|{
name|oriCtxs
operator|.
name|add
argument_list|(
name|ctxf
operator|.
name|binaryValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|oriCtxs
operator|.
name|size
argument_list|()
argument_list|,
name|contextSet
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|invalidTerm
range|:
name|invalidDocTerms
control|)
block|{
name|assertNotNull
argument_list|(
name|docs
operator|.
name|remove
argument_list|(
name|invalidTerm
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|docs
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWithDeletions
specifier|public
name|void
name|testWithDeletions
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|Map
operator|.
name|Entry
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Document
argument_list|>
argument_list|>
name|res
init|=
name|generateIndexDocuments
argument_list|(
name|atLeast
argument_list|(
literal|1000
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Document
argument_list|>
name|docs
init|=
name|res
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|invalidDocTerms
init|=
name|res
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Random
name|rand
init|=
name|random
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|termsToDel
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Document
name|doc
range|:
name|docs
operator|.
name|values
argument_list|()
control|)
block|{
name|StorableField
name|f
init|=
name|doc
operator|.
name|getField
argument_list|(
name|FIELD_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|rand
operator|.
name|nextBoolean
argument_list|()
operator|&&
name|f
operator|!=
literal|null
operator|&&
operator|!
name|invalidDocTerms
operator|.
name|contains
argument_list|(
name|f
operator|.
name|stringValue
argument_list|()
argument_list|)
condition|)
block|{
name|termsToDel
operator|.
name|add
argument_list|(
name|doc
operator|.
name|get
argument_list|(
name|FIELD_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Term
index|[]
name|delTerms
init|=
operator|new
name|Term
index|[
name|termsToDel
operator|.
name|size
argument_list|()
index|]
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
name|termsToDel
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|delTerms
index|[
name|i
index|]
operator|=
operator|new
name|Term
argument_list|(
name|FIELD_NAME
argument_list|,
name|termsToDel
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Term
name|delTerm
range|:
name|delTerms
control|)
block|{
name|writer
operator|.
name|deleteDocuments
argument_list|(
name|delTerm
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|termToDel
range|:
name|termsToDel
control|)
block|{
name|assertTrue
argument_list|(
literal|null
operator|!=
name|docs
operator|.
name|remove
argument_list|(
name|termToDel
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|IndexReader
name|ir
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ir
operator|.
name|numDocs
argument_list|()
argument_list|,
name|docs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Dictionary
name|dictionary
init|=
operator|new
name|DocumentDictionary
argument_list|(
name|ir
argument_list|,
name|FIELD_NAME
argument_list|,
name|WEIGHT_FIELD_NAME
argument_list|)
decl_stmt|;
name|InputIterator
name|inputIterator
init|=
name|dictionary
operator|.
name|getEntryIterator
argument_list|()
decl_stmt|;
name|BytesRef
name|f
decl_stmt|;
while|while
condition|(
operator|(
name|f
operator|=
name|inputIterator
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|Document
name|doc
init|=
name|docs
operator|.
name|remove
argument_list|(
name|f
operator|.
name|utf8ToString
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|equals
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|doc
operator|.
name|get
argument_list|(
name|FIELD_NAME
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Field
name|weightField
init|=
name|doc
operator|.
name|getField
argument_list|(
name|WEIGHT_FIELD_NAME
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|inputIterator
operator|.
name|weight
argument_list|()
argument_list|,
operator|(
name|weightField
operator|!=
literal|null
operator|)
condition|?
name|weightField
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
else|:
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|inputIterator
operator|.
name|payload
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|invalidTerm
range|:
name|invalidDocTerms
control|)
block|{
name|assertNotNull
argument_list|(
name|docs
operator|.
name|remove
argument_list|(
name|invalidTerm
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|docs
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultiValuedField
specifier|public
name|void
name|testMultiValuedField
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
name|random
argument_list|()
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Suggestion
argument_list|>
name|suggestions
init|=
name|indexMultiValuedDocuments
argument_list|(
name|atLeast
argument_list|(
literal|1000
argument_list|)
argument_list|,
name|writer
argument_list|)
decl_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|ir
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|Dictionary
name|dictionary
init|=
operator|new
name|DocumentDictionary
argument_list|(
name|ir
argument_list|,
name|FIELD_NAME
argument_list|,
name|WEIGHT_FIELD_NAME
argument_list|,
name|PAYLOAD_FIELD_NAME
argument_list|,
name|CONTEXT_FIELD_NAME
argument_list|)
decl_stmt|;
name|InputIterator
name|inputIterator
init|=
name|dictionary
operator|.
name|getEntryIterator
argument_list|()
decl_stmt|;
name|BytesRef
name|f
decl_stmt|;
name|Iterator
argument_list|<
name|Suggestion
argument_list|>
name|suggestionsIter
init|=
name|suggestions
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|f
operator|=
name|inputIterator
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|Suggestion
name|nextSuggestion
init|=
name|suggestionsIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|equals
argument_list|(
name|nextSuggestion
operator|.
name|term
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|weight
init|=
name|nextSuggestion
operator|.
name|weight
decl_stmt|;
name|assertEquals
argument_list|(
name|inputIterator
operator|.
name|weight
argument_list|()
argument_list|,
operator|(
name|weight
operator|!=
operator|-
literal|1
operator|)
condition|?
name|weight
else|:
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|inputIterator
operator|.
name|payload
argument_list|()
operator|.
name|equals
argument_list|(
name|nextSuggestion
operator|.
name|payload
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|inputIterator
operator|.
name|contexts
argument_list|()
operator|.
name|equals
argument_list|(
name|nextSuggestion
operator|.
name|contexts
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|suggestionsIter
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|indexMultiValuedDocuments
specifier|private
name|List
argument_list|<
name|Suggestion
argument_list|>
name|indexMultiValuedDocuments
parameter_list|(
name|int
name|numDocs
parameter_list|,
name|RandomIndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Suggestion
argument_list|>
name|suggestionList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numDocs
argument_list|)
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|field
decl_stmt|;
name|BytesRef
name|payloadValue
decl_stmt|;
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|contextValues
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|long
name|numericValue
init|=
operator|-
literal|1
decl_stmt|;
comment|//-1 for missing weight
name|BytesRef
name|term
decl_stmt|;
name|payloadValue
operator|=
operator|new
name|BytesRef
argument_list|(
literal|"payload_"
operator|+
name|i
argument_list|)
expr_stmt|;
name|field
operator|=
operator|new
name|StoredField
argument_list|(
name|PAYLOAD_FIELD_NAME
argument_list|,
name|payloadValue
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
if|if
condition|(
name|usually
argument_list|()
condition|)
block|{
name|numericValue
operator|=
literal|100
operator|+
name|i
expr_stmt|;
name|field
operator|=
operator|new
name|NumericDocValuesField
argument_list|(
name|WEIGHT_FIELD_NAME
argument_list|,
name|numericValue
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
name|int
name|numContexts
init|=
name|atLeast
argument_list|(
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numContexts
condition|;
name|j
operator|++
control|)
block|{
name|BytesRef
name|contextValue
init|=
operator|new
name|BytesRef
argument_list|(
literal|"context_"
operator|+
name|i
operator|+
literal|"_"
operator|+
name|j
argument_list|)
decl_stmt|;
name|field
operator|=
operator|new
name|StoredField
argument_list|(
name|CONTEXT_FIELD_NAME
argument_list|,
name|contextValue
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|contextValues
operator|.
name|add
argument_list|(
name|contextValue
argument_list|)
expr_stmt|;
block|}
name|int
name|numSuggestions
init|=
name|atLeast
argument_list|(
literal|2
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numSuggestions
condition|;
name|j
operator|++
control|)
block|{
name|term
operator|=
operator|new
name|BytesRef
argument_list|(
literal|"field_"
operator|+
name|i
operator|+
literal|"_"
operator|+
name|j
argument_list|)
expr_stmt|;
name|field
operator|=
operator|new
name|StoredField
argument_list|(
name|FIELD_NAME
argument_list|,
name|term
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|Suggestion
name|suggestionValue
init|=
operator|new
name|Suggestion
argument_list|()
decl_stmt|;
name|suggestionValue
operator|.
name|payload
operator|=
name|payloadValue
expr_stmt|;
name|suggestionValue
operator|.
name|contexts
operator|=
name|contextValues
expr_stmt|;
name|suggestionValue
operator|.
name|weight
operator|=
name|numericValue
expr_stmt|;
name|suggestionValue
operator|.
name|term
operator|=
name|term
expr_stmt|;
name|suggestionList
operator|.
name|add
argument_list|(
name|suggestionValue
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
return|return
name|suggestionList
return|;
block|}
DECL|class|Suggestion
specifier|private
class|class
name|Suggestion
block|{
DECL|field|weight
specifier|private
name|long
name|weight
decl_stmt|;
DECL|field|payload
specifier|private
name|BytesRef
name|payload
decl_stmt|;
DECL|field|contexts
specifier|private
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|contexts
decl_stmt|;
DECL|field|term
specifier|private
name|BytesRef
name|term
decl_stmt|;
block|}
block|}
end_class
end_unit
