begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.idversion
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|idversion
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
name|HashMap
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
name|CannedTokenStream
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|PayloadAttribute
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
name|codecs
operator|.
name|Codec
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
name|FieldType
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
name|BasePostingsFormatTestCase
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
name|DocsEnum
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
name|FieldInfo
operator|.
name|IndexOptions
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
name|PerThreadPKLookup
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
comment|/**  * Basic tests for IDVersionPostingsFormat  */
end_comment
begin_class
DECL|class|TestIDVersionPostingsFormat
specifier|public
class|class
name|TestIDVersionPostingsFormat
extends|extends
name|LuceneTestCase
block|{
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Exception
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
name|TEST_VERSION_CURRENT
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
name|setCodec
argument_list|(
name|TestUtil
operator|.
name|alwaysPostingsFormat
argument_list|(
operator|new
name|IDVersionPostingsFormat
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|w
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
name|makeIDField
argument_list|(
literal|"id0"
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|makeIDField
argument_list|(
literal|"id1"
argument_list|,
literal|110
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|IDVersionSegmentTermsEnum
name|termsEnum
init|=
operator|(
name|IDVersionSegmentTermsEnum
operator|)
name|r
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reader
argument_list|()
operator|.
name|fields
argument_list|()
operator|.
name|terms
argument_list|(
literal|"id"
argument_list|)
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"id0"
argument_list|)
argument_list|,
literal|50
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"id0"
argument_list|)
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"id0"
argument_list|)
argument_list|,
literal|101
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"id1"
argument_list|)
argument_list|,
literal|50
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"id1"
argument_list|)
argument_list|,
literal|110
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"id1"
argument_list|)
argument_list|,
literal|111
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
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
comment|// nocommit vary the style of iD; sometimes fixed-length ids, timestamp, zero filled, seuqential, random, etc.
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|Exception
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
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// nocommit randomize the block sizes:
name|iwc
operator|.
name|setCodec
argument_list|(
name|TestUtil
operator|.
name|alwaysPostingsFormat
argument_list|(
operator|new
name|IDVersionPostingsFormat
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// nocommit put back
comment|//RandomIndexWriter w = new RandomIndexWriter(random(), dir, iwc);
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|idValues
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|docUpto
init|=
literal|0
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
literal|"TEST: numDocs="
operator|+
name|numDocs
argument_list|)
expr_stmt|;
block|}
name|long
name|version
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|docUpto
operator|<
name|numDocs
condition|)
block|{
comment|// nocommit add deletes in
comment|// nocommit randomRealisticUniode / full binary
name|String
name|idValue
init|=
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|idValues
operator|.
name|containsKey
argument_list|(
name|idValue
argument_list|)
condition|)
block|{
continue|continue;
block|}
comment|//long version = random().nextLong()& 0x7fffffffffffffffL;
name|version
operator|++
expr_stmt|;
name|idValues
operator|.
name|put
argument_list|(
name|idValue
argument_list|,
name|version
argument_list|)
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
name|idValue
operator|+
literal|" -> "
operator|+
name|version
argument_list|)
expr_stmt|;
block|}
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
name|makeIDField
argument_list|(
name|idValue
argument_list|,
name|version
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|docUpto
operator|++
expr_stmt|;
block|}
comment|//IndexReader r = w.getReader();
name|IndexReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|PerThreadVersionPKLookup
name|lookup
init|=
operator|new
name|PerThreadVersionPKLookup
argument_list|(
name|r
argument_list|,
literal|"id"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|>
name|idValuesList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|idValues
operator|.
name|entrySet
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|iters
init|=
name|numDocs
operator|*
literal|5
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
name|iters
condition|;
name|iter
operator|++
control|)
block|{
name|String
name|idValue
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|idValue
operator|=
name|idValuesList
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numDocs
argument_list|)
argument_list|)
operator|.
name|getKey
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|idValue
operator|=
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|BytesRef
name|idValueBytes
init|=
operator|new
name|BytesRef
argument_list|(
name|idValue
argument_list|)
decl_stmt|;
name|Long
name|expectedVersion
init|=
name|idValues
operator|.
name|get
argument_list|(
name|idValue
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
literal|"\nTEST: iter="
operator|+
name|iter
operator|+
literal|" id="
operator|+
name|idValue
operator|+
literal|" expectedVersion="
operator|+
name|expectedVersion
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|expectedVersion
operator|==
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|lookup
operator|.
name|lookup
argument_list|(
name|idValueBytes
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
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
literal|"  lookup exact version (should be found)"
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|lookup
operator|.
name|lookup
argument_list|(
name|idValueBytes
argument_list|,
name|expectedVersion
operator|.
name|longValue
argument_list|()
argument_list|)
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
literal|"  lookup version+1 (should not be found)"
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|lookup
operator|.
name|lookup
argument_list|(
name|idValueBytes
argument_list|,
name|expectedVersion
operator|.
name|longValue
argument_list|()
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
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
DECL|class|PerThreadVersionPKLookup
specifier|private
specifier|static
class|class
name|PerThreadVersionPKLookup
extends|extends
name|PerThreadPKLookup
block|{
DECL|method|PerThreadVersionPKLookup
specifier|public
name|PerThreadVersionPKLookup
parameter_list|(
name|IndexReader
name|r
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|r
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
comment|/** Returns docID if found, else -1. */
DECL|method|lookup
specifier|public
name|int
name|lookup
parameter_list|(
name|BytesRef
name|id
parameter_list|,
name|long
name|version
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|seg
init|=
literal|0
init|;
name|seg
operator|<
name|numSegs
condition|;
name|seg
operator|++
control|)
block|{
if|if
condition|(
operator|(
operator|(
name|IDVersionSegmentTermsEnum
operator|)
name|termsEnums
index|[
name|seg
index|]
operator|)
operator|.
name|seekExact
argument_list|(
name|id
argument_list|,
name|version
argument_list|)
condition|)
block|{
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
literal|"  found in seg="
operator|+
name|termsEnums
index|[
name|seg
index|]
argument_list|)
expr_stmt|;
block|}
name|docsEnums
index|[
name|seg
index|]
operator|=
name|termsEnums
index|[
name|seg
index|]
operator|.
name|docs
argument_list|(
name|liveDocs
index|[
name|seg
index|]
argument_list|,
name|docsEnums
index|[
name|seg
index|]
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|int
name|docID
init|=
name|docsEnums
index|[
name|seg
index|]
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|docID
operator|!=
name|DocsEnum
operator|.
name|NO_MORE_DOCS
condition|)
block|{
return|return
name|docBases
index|[
name|seg
index|]
operator|+
name|docID
return|;
block|}
assert|assert
name|hasDeletions
assert|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
block|}
comment|/** Produces a single token from the provided value, with the provided payload. */
DECL|class|StringAndPayloadField
specifier|private
specifier|static
class|class
name|StringAndPayloadField
extends|extends
name|Field
block|{
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|FieldType
name|TYPE
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|TYPE
operator|.
name|setIndexed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
expr_stmt|;
name|TYPE
operator|.
name|setTokenized
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
DECL|field|payload
specifier|private
specifier|final
name|BytesRef
name|payload
decl_stmt|;
DECL|method|StringAndPayloadField
specifier|public
name|StringAndPayloadField
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|,
name|BytesRef
name|payload
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|TYPE
argument_list|)
expr_stmt|;
name|this
operator|.
name|payload
operator|=
name|payload
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|TokenStream
name|reuse
parameter_list|)
throws|throws
name|IOException
block|{
name|SingleTokenWithPayloadTokenStream
name|ts
decl_stmt|;
if|if
condition|(
name|reuse
operator|instanceof
name|SingleTokenWithPayloadTokenStream
condition|)
block|{
name|ts
operator|=
operator|(
name|SingleTokenWithPayloadTokenStream
operator|)
name|reuse
expr_stmt|;
block|}
else|else
block|{
name|ts
operator|=
operator|new
name|SingleTokenWithPayloadTokenStream
argument_list|()
expr_stmt|;
block|}
name|ts
operator|.
name|setValue
argument_list|(
operator|(
name|String
operator|)
name|fieldsData
argument_list|,
name|payload
argument_list|)
expr_stmt|;
return|return
name|ts
return|;
block|}
block|}
DECL|class|SingleTokenWithPayloadTokenStream
specifier|private
specifier|static
specifier|final
class|class
name|SingleTokenWithPayloadTokenStream
extends|extends
name|TokenStream
block|{
DECL|field|termAttribute
specifier|private
specifier|final
name|CharTermAttribute
name|termAttribute
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|payloadAttribute
specifier|private
specifier|final
name|PayloadAttribute
name|payloadAttribute
init|=
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|used
specifier|private
name|boolean
name|used
init|=
literal|false
decl_stmt|;
DECL|field|value
specifier|private
name|String
name|value
init|=
literal|null
decl_stmt|;
DECL|field|payload
specifier|private
name|BytesRef
name|payload
decl_stmt|;
comment|/** Creates a new TokenStream that returns a String+payload as single token.      *<p>Warning: Does not initialize the value, you must call      * {@link #setValue(String)} afterwards!      */
DECL|method|SingleTokenWithPayloadTokenStream
name|SingleTokenWithPayloadTokenStream
parameter_list|()
block|{     }
comment|/** Sets the string value. */
DECL|method|setValue
name|void
name|setValue
parameter_list|(
name|String
name|value
parameter_list|,
name|BytesRef
name|payload
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|payload
operator|=
name|payload
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
block|{
if|if
condition|(
name|used
condition|)
block|{
return|return
literal|false
return|;
block|}
name|clearAttributes
argument_list|()
expr_stmt|;
name|termAttribute
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|payloadAttribute
operator|.
name|setPayload
argument_list|(
name|payload
argument_list|)
expr_stmt|;
name|used
operator|=
literal|true
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|used
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|value
operator|=
literal|null
expr_stmt|;
name|payload
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|makeIDField
specifier|private
specifier|static
name|Field
name|makeIDField
parameter_list|(
name|String
name|id
parameter_list|,
name|long
name|version
parameter_list|)
block|{
name|BytesRef
name|payload
init|=
operator|new
name|BytesRef
argument_list|(
literal|8
argument_list|)
decl_stmt|;
name|payload
operator|.
name|length
operator|=
literal|8
expr_stmt|;
name|IDVersionPostingsFormat
operator|.
name|longToBytes
argument_list|(
name|version
argument_list|,
name|payload
argument_list|)
expr_stmt|;
return|return
operator|new
name|StringAndPayloadField
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|,
name|payload
argument_list|)
return|;
comment|/*     Field field = newTextField("id", "", Field.Store.NO);     Token token = new Token(id, 0, id.length());     token.setPayload(payload);     field.setTokenStream(new CannedTokenStream(token));     return field;     */
block|}
block|}
end_class
end_unit
