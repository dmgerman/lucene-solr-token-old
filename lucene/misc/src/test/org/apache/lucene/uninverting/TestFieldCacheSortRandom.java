begin_unit
begin_package
DECL|package|org.apache.lucene.uninverting
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|uninverting
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
name|ArrayList
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
name|Comparator
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
name|Objects
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
name|LegacyIntField
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
name|StringField
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
name|DocValues
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
name|LeafReaderContext
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
name|NumericDocValues
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
name|search
operator|.
name|ConstantScoreQuery
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
name|ConstantScoreScorer
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
name|ConstantScoreWeight
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
name|FieldDoc
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
name|IndexSearcher
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
name|Query
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
name|Scorer
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
name|Sort
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
name|SortField
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
name|TopFieldDocs
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
name|Weight
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
name|uninverting
operator|.
name|UninvertingReader
operator|.
name|Type
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
name|BitSetIterator
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
name|FixedBitSet
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|TestUtil
import|;
end_import
begin_comment
comment|/** random sorting tests with uninversion */
end_comment
begin_class
DECL|class|TestFieldCacheSortRandom
specifier|public
class|class
name|TestFieldCacheSortRandom
extends|extends
name|LuceneTestCase
block|{
DECL|method|testRandomStringSort
specifier|public
name|void
name|testRandomStringSort
parameter_list|()
throws|throws
name|Exception
block|{
name|testRandomStringSort
argument_list|(
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandomStringValSort
specifier|public
name|void
name|testRandomStringValSort
parameter_list|()
throws|throws
name|Exception
block|{
name|testRandomStringSort
argument_list|(
name|SortField
operator|.
name|Type
operator|.
name|STRING_VAL
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandomStringSort
specifier|private
name|void
name|testRandomStringSort
parameter_list|(
name|SortField
operator|.
name|Type
name|type
parameter_list|)
throws|throws
name|Exception
block|{
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|NUM_DOCS
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
specifier|final
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|allowDups
init|=
name|random
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|seen
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|maxLength
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|5
argument_list|,
literal|100
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: NUM_DOCS="
operator|+
name|NUM_DOCS
operator|+
literal|" maxLength="
operator|+
name|maxLength
operator|+
literal|" allowDups="
operator|+
name|allowDups
argument_list|)
expr_stmt|;
block|}
name|int
name|numDocs
init|=
literal|0
decl_stmt|;
specifier|final
name|List
argument_list|<
name|BytesRef
argument_list|>
name|docValues
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// TODO: deletions
while|while
condition|(
name|numDocs
operator|<
name|NUM_DOCS
condition|)
block|{
specifier|final
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// 10% of the time, the document is missing the value:
specifier|final
name|BytesRef
name|br
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|!=
literal|7
condition|)
block|{
specifier|final
name|String
name|s
decl_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|s
operator|=
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|,
name|maxLength
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|s
operator|=
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|,
name|maxLength
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|allowDups
condition|)
block|{
if|if
condition|(
name|seen
operator|.
name|contains
argument_list|(
name|s
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|seen
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  "
operator|+
name|numDocs
operator|+
literal|": s="
operator|+
name|s
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"stringdv"
argument_list|,
name|s
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|docValues
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|br
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  "
operator|+
name|numDocs
operator|+
literal|":<missing>"
argument_list|)
expr_stmt|;
block|}
name|docValues
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LegacyIntField
argument_list|(
literal|"id"
argument_list|,
name|numDocs
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|numDocs
operator|++
expr_stmt|;
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|40
argument_list|)
operator|==
literal|17
condition|)
block|{
comment|// force flush
name|writer
operator|.
name|getReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|UninvertingReader
operator|.
name|Type
argument_list|>
name|mapping
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|mapping
operator|.
name|put
argument_list|(
literal|"stringdv"
argument_list|,
name|Type
operator|.
name|SORTED
argument_list|)
expr_stmt|;
name|mapping
operator|.
name|put
argument_list|(
literal|"id"
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|)
expr_stmt|;
specifier|final
name|IndexReader
name|r
init|=
name|UninvertingReader
operator|.
name|wrap
argument_list|(
name|writer
operator|.
name|getReader
argument_list|()
argument_list|,
name|mapping
argument_list|)
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  reader="
operator|+
name|r
argument_list|)
expr_stmt|;
block|}
specifier|final
name|IndexSearcher
name|s
init|=
name|newSearcher
argument_list|(
name|r
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|int
name|ITERS
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|ITERS
condition|;
name|iter
operator|++
control|)
block|{
specifier|final
name|boolean
name|reverse
init|=
name|random
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|TopFieldDocs
name|hits
decl_stmt|;
specifier|final
name|SortField
name|sf
decl_stmt|;
specifier|final
name|boolean
name|sortMissingLast
decl_stmt|;
specifier|final
name|boolean
name|missingIsNull
decl_stmt|;
name|sf
operator|=
operator|new
name|SortField
argument_list|(
literal|"stringdv"
argument_list|,
name|type
argument_list|,
name|reverse
argument_list|)
expr_stmt|;
name|sortMissingLast
operator|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
expr_stmt|;
name|missingIsNull
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|sortMissingLast
condition|)
block|{
name|sf
operator|.
name|setMissingValue
argument_list|(
name|SortField
operator|.
name|STRING_LAST
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Sort
name|sort
decl_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|sort
operator|=
operator|new
name|Sort
argument_list|(
name|sf
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sort
operator|=
operator|new
name|Sort
argument_list|(
name|sf
argument_list|,
name|SortField
operator|.
name|FIELD_DOC
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|hitCount
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
name|r
operator|.
name|maxDoc
argument_list|()
operator|+
literal|20
argument_list|)
decl_stmt|;
specifier|final
name|RandomQuery
name|f
init|=
operator|new
name|RandomQuery
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|,
name|random
operator|.
name|nextFloat
argument_list|()
argument_list|,
name|docValues
argument_list|)
decl_stmt|;
name|int
name|queryType
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|queryType
operator|==
literal|0
condition|)
block|{
name|hits
operator|=
name|s
operator|.
name|search
argument_list|(
operator|new
name|ConstantScoreQuery
argument_list|(
name|f
argument_list|)
argument_list|,
name|hitCount
argument_list|,
name|sort
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|hits
operator|=
name|s
operator|.
name|search
argument_list|(
name|f
argument_list|,
name|hitCount
argument_list|,
name|sort
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: iter="
operator|+
name|iter
operator|+
literal|" "
operator|+
name|hits
operator|.
name|totalHits
operator|+
literal|" hits; topN="
operator|+
name|hitCount
operator|+
literal|"; reverse="
operator|+
name|reverse
operator|+
literal|"; sortMissingLast="
operator|+
name|sortMissingLast
operator|+
literal|" sort="
operator|+
name|sort
argument_list|)
expr_stmt|;
block|}
comment|// Compute expected results:
name|Collections
operator|.
name|sort
argument_list|(
name|f
operator|.
name|matchValues
argument_list|,
operator|new
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|BytesRef
name|a
parameter_list|,
name|BytesRef
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|b
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
if|if
condition|(
name|sortMissingLast
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|b
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|sortMissingLast
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
literal|1
return|;
block|}
block|}
else|else
block|{
return|return
name|a
operator|.
name|compareTo
argument_list|(
name|b
argument_list|)
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|reverse
condition|)
block|{
name|Collections
operator|.
name|reverse
argument_list|(
name|f
operator|.
name|matchValues
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|BytesRef
argument_list|>
name|expected
init|=
name|f
operator|.
name|matchValues
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  expected:"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|expected
operator|.
name|size
argument_list|()
condition|;
name|idx
operator|++
control|)
block|{
name|BytesRef
name|br
init|=
name|expected
operator|.
name|get
argument_list|(
name|idx
argument_list|)
decl_stmt|;
if|if
condition|(
name|br
operator|==
literal|null
operator|&&
name|missingIsNull
operator|==
literal|false
condition|)
block|{
name|br
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    "
operator|+
name|idx
operator|+
literal|": "
operator|+
operator|(
name|br
operator|==
literal|null
condition|?
literal|"<missing>"
else|:
name|br
operator|.
name|utf8ToString
argument_list|()
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|idx
operator|==
name|hitCount
operator|-
literal|1
condition|)
block|{
break|break;
block|}
block|}
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  actual:"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|hitIDX
init|=
literal|0
init|;
name|hitIDX
operator|<
name|hits
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|hitIDX
operator|++
control|)
block|{
specifier|final
name|FieldDoc
name|fd
init|=
operator|(
name|FieldDoc
operator|)
name|hits
operator|.
name|scoreDocs
index|[
name|hitIDX
index|]
decl_stmt|;
name|BytesRef
name|br
init|=
operator|(
name|BytesRef
operator|)
name|fd
operator|.
name|fields
index|[
literal|0
index|]
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    "
operator|+
name|hitIDX
operator|+
literal|": "
operator|+
operator|(
name|br
operator|==
literal|null
condition|?
literal|"<missing>"
else|:
name|br
operator|.
name|utf8ToString
argument_list|()
operator|)
operator|+
literal|" id="
operator|+
name|s
operator|.
name|doc
argument_list|(
name|fd
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|hitIDX
init|=
literal|0
init|;
name|hitIDX
operator|<
name|hits
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|hitIDX
operator|++
control|)
block|{
specifier|final
name|FieldDoc
name|fd
init|=
operator|(
name|FieldDoc
operator|)
name|hits
operator|.
name|scoreDocs
index|[
name|hitIDX
index|]
decl_stmt|;
name|BytesRef
name|br
init|=
name|expected
operator|.
name|get
argument_list|(
name|hitIDX
argument_list|)
decl_stmt|;
if|if
condition|(
name|br
operator|==
literal|null
operator|&&
name|missingIsNull
operator|==
literal|false
condition|)
block|{
name|br
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
block|}
comment|// Normally, the old codecs (that don't support
comment|// docsWithField via doc values) will always return
comment|// an empty BytesRef for the missing case; however,
comment|// if all docs in a given segment were missing, in
comment|// that case it will return null!  So we must map
comment|// null here, too:
name|BytesRef
name|br2
init|=
operator|(
name|BytesRef
operator|)
name|fd
operator|.
name|fields
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|br2
operator|==
literal|null
operator|&&
name|missingIsNull
operator|==
literal|false
condition|)
block|{
name|br2
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|br
argument_list|,
name|br2
argument_list|)
expr_stmt|;
block|}
block|}
name|r
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
DECL|class|RandomQuery
specifier|private
specifier|static
class|class
name|RandomQuery
extends|extends
name|Query
block|{
DECL|field|seed
specifier|private
specifier|final
name|long
name|seed
decl_stmt|;
DECL|field|density
specifier|private
name|float
name|density
decl_stmt|;
DECL|field|docValues
specifier|private
specifier|final
name|List
argument_list|<
name|BytesRef
argument_list|>
name|docValues
decl_stmt|;
DECL|field|matchValues
specifier|public
specifier|final
name|List
argument_list|<
name|BytesRef
argument_list|>
name|matchValues
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
comment|// density should be 0.0 ... 1.0
DECL|method|RandomQuery
specifier|public
name|RandomQuery
parameter_list|(
name|long
name|seed
parameter_list|,
name|float
name|density
parameter_list|,
name|List
argument_list|<
name|BytesRef
argument_list|>
name|docValues
parameter_list|)
block|{
name|this
operator|.
name|seed
operator|=
name|seed
expr_stmt|;
name|this
operator|.
name|density
operator|=
name|density
expr_stmt|;
name|this
operator|.
name|docValues
operator|=
name|docValues
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ConstantScoreWeight
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|seed
operator|^
name|context
operator|.
name|docBase
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxDoc
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
specifier|final
name|NumericDocValues
name|idSource
init|=
name|DocValues
operator|.
name|getNumeric
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
literal|"id"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|idSource
argument_list|)
expr_stmt|;
specifier|final
name|FixedBitSet
name|bits
init|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|docID
init|=
literal|0
init|;
name|docID
operator|<
name|maxDoc
condition|;
name|docID
operator|++
control|)
block|{
if|if
condition|(
name|random
operator|.
name|nextFloat
argument_list|()
operator|<=
name|density
condition|)
block|{
name|bits
operator|.
name|set
argument_list|(
name|docID
argument_list|)
expr_stmt|;
comment|//System.out.println("  acc id=" + idSource.getInt(docID) + " docID=" + docID);
name|matchValues
operator|.
name|add
argument_list|(
name|docValues
operator|.
name|get
argument_list|(
operator|(
name|int
operator|)
name|idSource
operator|.
name|get
argument_list|(
name|docID
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|ConstantScoreScorer
argument_list|(
name|this
argument_list|,
name|score
argument_list|()
argument_list|,
operator|new
name|BitSetIterator
argument_list|(
name|bits
argument_list|,
name|bits
operator|.
name|approximateCardinality
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
literal|"RandomFilter(density="
operator|+
name|density
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
name|RandomQuery
name|other
init|=
operator|(
name|RandomQuery
operator|)
name|obj
decl_stmt|;
return|return
name|seed
operator|==
name|other
operator|.
name|seed
operator|&&
name|docValues
operator|==
name|other
operator|.
name|docValues
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|h
init|=
name|Objects
operator|.
name|hash
argument_list|(
name|seed
argument_list|,
name|density
argument_list|)
decl_stmt|;
name|h
operator|=
literal|31
operator|*
name|h
operator|+
name|System
operator|.
name|identityHashCode
argument_list|(
name|docValues
argument_list|)
expr_stmt|;
name|h
operator|=
literal|31
operator|*
name|h
operator|+
name|super
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|h
return|;
block|}
block|}
block|}
end_class
end_unit
