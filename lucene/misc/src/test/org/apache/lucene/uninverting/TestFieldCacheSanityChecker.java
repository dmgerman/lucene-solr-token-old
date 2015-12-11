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
comment|/*  * Copyright 2009 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|LegacyDoubleField
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
name|LegacyFloatField
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
name|LegacyLongField
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
name|LeafReader
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
name|IndexWriter
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
name|MultiReader
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
name|SlowCompositeReaderWrapper
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
name|FieldCacheSanityChecker
operator|.
name|Insanity
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
name|FieldCacheSanityChecker
operator|.
name|InsanityType
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
begin_class
DECL|class|TestFieldCacheSanityChecker
specifier|public
class|class
name|TestFieldCacheSanityChecker
extends|extends
name|LuceneTestCase
block|{
DECL|field|readerA
specifier|protected
name|LeafReader
name|readerA
decl_stmt|;
DECL|field|readerB
specifier|protected
name|LeafReader
name|readerB
decl_stmt|;
DECL|field|readerX
specifier|protected
name|LeafReader
name|readerX
decl_stmt|;
DECL|field|readerAclone
specifier|protected
name|LeafReader
name|readerAclone
decl_stmt|;
DECL|field|dirA
DECL|field|dirB
specifier|protected
name|Directory
name|dirA
decl_stmt|,
name|dirB
decl_stmt|;
DECL|field|NUM_DOCS
specifier|private
specifier|static
specifier|final
name|int
name|NUM_DOCS
init|=
literal|1000
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|dirA
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|dirB
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|IndexWriter
name|wA
init|=
operator|new
name|IndexWriter
argument_list|(
name|dirA
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|IndexWriter
name|wB
init|=
operator|new
name|IndexWriter
argument_list|(
name|dirB
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|theLong
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
name|double
name|theDouble
init|=
name|Double
operator|.
name|MAX_VALUE
decl_stmt|;
name|int
name|theInt
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
name|float
name|theFloat
init|=
name|Float
operator|.
name|MAX_VALUE
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
name|NUM_DOCS
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
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LegacyLongField
argument_list|(
literal|"theLong"
argument_list|,
name|theLong
operator|--
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LegacyDoubleField
argument_list|(
literal|"theDouble"
argument_list|,
name|theDouble
operator|--
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LegacyIntField
argument_list|(
literal|"theInt"
argument_list|,
name|theInt
operator|--
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LegacyFloatField
argument_list|(
literal|"theFloat"
argument_list|,
name|theFloat
operator|--
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
literal|0
operator|==
name|i
operator|%
literal|3
condition|)
block|{
name|wA
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|wB
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
name|wA
operator|.
name|close
argument_list|()
expr_stmt|;
name|wB
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryReader
name|rA
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dirA
argument_list|)
decl_stmt|;
name|readerA
operator|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|rA
argument_list|)
expr_stmt|;
name|readerAclone
operator|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|rA
argument_list|)
expr_stmt|;
name|readerA
operator|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dirA
argument_list|)
argument_list|)
expr_stmt|;
name|readerB
operator|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dirB
argument_list|)
argument_list|)
expr_stmt|;
name|readerX
operator|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
operator|new
name|MultiReader
argument_list|(
name|readerA
argument_list|,
name|readerB
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|readerA
operator|.
name|close
argument_list|()
expr_stmt|;
name|readerAclone
operator|.
name|close
argument_list|()
expr_stmt|;
name|readerB
operator|.
name|close
argument_list|()
expr_stmt|;
name|readerX
operator|.
name|close
argument_list|()
expr_stmt|;
name|dirA
operator|.
name|close
argument_list|()
expr_stmt|;
name|dirB
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testSanity
specifier|public
name|void
name|testSanity
parameter_list|()
throws|throws
name|IOException
block|{
name|FieldCache
name|cache
init|=
name|FieldCache
operator|.
name|DEFAULT
decl_stmt|;
name|cache
operator|.
name|purgeAllCaches
argument_list|()
expr_stmt|;
name|cache
operator|.
name|getNumerics
argument_list|(
name|readerA
argument_list|,
literal|"theDouble"
argument_list|,
name|FieldCache
operator|.
name|NUMERIC_UTILS_DOUBLE_PARSER
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cache
operator|.
name|getNumerics
argument_list|(
name|readerAclone
argument_list|,
literal|"theDouble"
argument_list|,
name|FieldCache
operator|.
name|NUMERIC_UTILS_DOUBLE_PARSER
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cache
operator|.
name|getNumerics
argument_list|(
name|readerB
argument_list|,
literal|"theDouble"
argument_list|,
name|FieldCache
operator|.
name|NUMERIC_UTILS_DOUBLE_PARSER
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cache
operator|.
name|getNumerics
argument_list|(
name|readerX
argument_list|,
literal|"theInt"
argument_list|,
name|FieldCache
operator|.
name|NUMERIC_UTILS_INT_PARSER
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// // //
name|Insanity
index|[]
name|insanity
init|=
name|FieldCacheSanityChecker
operator|.
name|checkSanity
argument_list|(
name|cache
operator|.
name|getCacheEntries
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0
operator|<
name|insanity
operator|.
name|length
condition|)
name|dumpArray
argument_list|(
name|getTestClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"#"
operator|+
name|getTestName
argument_list|()
operator|+
literal|" INSANITY"
argument_list|,
name|insanity
argument_list|,
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"shouldn't be any cache insanity"
argument_list|,
literal|0
argument_list|,
name|insanity
operator|.
name|length
argument_list|)
expr_stmt|;
name|cache
operator|.
name|purgeAllCaches
argument_list|()
expr_stmt|;
block|}
DECL|method|testInsanity1
specifier|public
name|void
name|testInsanity1
parameter_list|()
throws|throws
name|IOException
block|{
name|FieldCache
name|cache
init|=
name|FieldCache
operator|.
name|DEFAULT
decl_stmt|;
name|cache
operator|.
name|purgeAllCaches
argument_list|()
expr_stmt|;
name|cache
operator|.
name|getNumerics
argument_list|(
name|readerX
argument_list|,
literal|"theInt"
argument_list|,
name|FieldCache
operator|.
name|NUMERIC_UTILS_INT_PARSER
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cache
operator|.
name|getTerms
argument_list|(
name|readerX
argument_list|,
literal|"theInt"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// // //
name|Insanity
index|[]
name|insanity
init|=
name|FieldCacheSanityChecker
operator|.
name|checkSanity
argument_list|(
name|cache
operator|.
name|getCacheEntries
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"wrong number of cache errors"
argument_list|,
literal|1
argument_list|,
name|insanity
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong type of cache error"
argument_list|,
name|InsanityType
operator|.
name|VALUEMISMATCH
argument_list|,
name|insanity
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong number of entries in cache error"
argument_list|,
literal|2
argument_list|,
name|insanity
index|[
literal|0
index|]
operator|.
name|getCacheEntries
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// we expect bad things, don't let tearDown complain about them
name|cache
operator|.
name|purgeAllCaches
argument_list|()
expr_stmt|;
block|}
DECL|method|testInsanity2
specifier|public
name|void
name|testInsanity2
parameter_list|()
throws|throws
name|IOException
block|{
name|FieldCache
name|cache
init|=
name|FieldCache
operator|.
name|DEFAULT
decl_stmt|;
name|cache
operator|.
name|purgeAllCaches
argument_list|()
expr_stmt|;
name|cache
operator|.
name|getTerms
argument_list|(
name|readerA
argument_list|,
literal|"theInt"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cache
operator|.
name|getTerms
argument_list|(
name|readerB
argument_list|,
literal|"theInt"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cache
operator|.
name|getTerms
argument_list|(
name|readerX
argument_list|,
literal|"theInt"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// // //
name|Insanity
index|[]
name|insanity
init|=
name|FieldCacheSanityChecker
operator|.
name|checkSanity
argument_list|(
name|cache
operator|.
name|getCacheEntries
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"wrong number of cache errors"
argument_list|,
literal|1
argument_list|,
name|insanity
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong type of cache error"
argument_list|,
name|InsanityType
operator|.
name|SUBREADER
argument_list|,
name|insanity
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong number of entries in cache error"
argument_list|,
literal|3
argument_list|,
name|insanity
index|[
literal|0
index|]
operator|.
name|getCacheEntries
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// we expect bad things, don't let tearDown complain about them
name|cache
operator|.
name|purgeAllCaches
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
