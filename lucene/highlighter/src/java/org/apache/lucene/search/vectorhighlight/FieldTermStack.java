begin_unit
begin_package
DECL|package|org.apache.lucene.search.vectorhighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|vectorhighlight
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
name|Collections
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
name|LinkedList
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
name|PostingsEnum
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
name|Fields
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
name|index
operator|.
name|Terms
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
name|CharsRefBuilder
import|;
end_import
begin_comment
comment|/**  *<code>FieldTermStack</code> is a stack that keeps query terms in the specified field  * of the document to be highlighted.  */
end_comment
begin_class
DECL|class|FieldTermStack
specifier|public
class|class
name|FieldTermStack
block|{
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|termList
name|LinkedList
argument_list|<
name|TermInfo
argument_list|>
name|termList
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
comment|//public static void main( String[] args ) throws Exception {
comment|//  Analyzer analyzer = new WhitespaceAnalyzer(Version.LATEST);
comment|//  QueryParser parser = new QueryParser(Version.LATEST,  "f", analyzer );
comment|//  Query query = parser.parse( "a x:b" );
comment|//  FieldQuery fieldQuery = new FieldQuery( query, true, false );
comment|//  Directory dir = new RAMDirectory();
comment|//  IndexWriter writer = new IndexWriter(dir, new IndexWriterConfig(Version.LATEST, analyzer));
comment|//  Document doc = new Document();
comment|//  FieldType ft = new FieldType(TextField.TYPE_STORED);
comment|//  ft.setStoreTermVectors(true);
comment|//  ft.setStoreTermVectorOffsets(true);
comment|//  ft.setStoreTermVectorPositions(true);
comment|//  doc.add( new Field( "f", ft, "a a a b b c a b b c d e f" ) );
comment|//  doc.add( new Field( "f", ft, "b a b a f" ) );
comment|//  writer.addDocument( doc );
comment|//  writer.close();
comment|//  IndexReader reader = IndexReader.open(dir1);
comment|//  new FieldTermStack( reader, 0, "f", fieldQuery );
comment|//  reader.close();
comment|//}
comment|/**    * a constructor.    *     * @param reader IndexReader of the index    * @param docId document id to be highlighted    * @param fieldName field of the document to be highlighted    * @param fieldQuery FieldQuery object    * @throws IOException If there is a low-level I/O error    */
DECL|method|FieldTermStack
specifier|public
name|FieldTermStack
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docId
parameter_list|,
name|String
name|fieldName
parameter_list|,
specifier|final
name|FieldQuery
name|fieldQuery
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|termSet
init|=
name|fieldQuery
operator|.
name|getTermSet
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
comment|// just return to make null snippet if un-matched fieldName specified when fieldMatch == true
if|if
condition|(
name|termSet
operator|==
literal|null
condition|)
return|return;
specifier|final
name|Fields
name|vectors
init|=
name|reader
operator|.
name|getTermVectors
argument_list|(
name|docId
argument_list|)
decl_stmt|;
if|if
condition|(
name|vectors
operator|==
literal|null
condition|)
block|{
comment|// null snippet
return|return;
block|}
specifier|final
name|Terms
name|vector
init|=
name|vectors
operator|.
name|terms
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|vector
operator|==
literal|null
operator|||
name|vector
operator|.
name|hasPositions
argument_list|()
operator|==
literal|false
condition|)
block|{
comment|// null snippet
return|return;
block|}
specifier|final
name|CharsRefBuilder
name|spare
init|=
operator|new
name|CharsRefBuilder
argument_list|()
decl_stmt|;
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|vector
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|PostingsEnum
name|dpEnum
init|=
literal|null
decl_stmt|;
name|BytesRef
name|text
decl_stmt|;
name|int
name|numDocs
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|text
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|spare
operator|.
name|copyUTF8Bytes
argument_list|(
name|text
argument_list|)
expr_stmt|;
specifier|final
name|String
name|term
init|=
name|spare
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|termSet
operator|.
name|contains
argument_list|(
name|term
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|dpEnum
operator|=
name|termsEnum
operator|.
name|postings
argument_list|(
literal|null
argument_list|,
name|dpEnum
argument_list|,
name|PostingsEnum
operator|.
name|POSITIONS
argument_list|)
expr_stmt|;
name|dpEnum
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
comment|// For weight look here: http://lucene.apache.org/core/3_6_0/api/core/org/apache/lucene/search/DefaultSimilarity.html
specifier|final
name|float
name|weight
init|=
call|(
name|float
call|)
argument_list|(
name|Math
operator|.
name|log
argument_list|(
name|numDocs
operator|/
call|(
name|double
call|)
argument_list|(
name|reader
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
name|text
argument_list|)
argument_list|)
operator|+
literal|1
argument_list|)
argument_list|)
operator|+
literal|1.0
argument_list|)
decl_stmt|;
specifier|final
name|int
name|freq
init|=
name|dpEnum
operator|.
name|freq
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
name|freq
condition|;
name|i
operator|++
control|)
block|{
name|int
name|pos
init|=
name|dpEnum
operator|.
name|nextPosition
argument_list|()
decl_stmt|;
if|if
condition|(
name|dpEnum
operator|.
name|startOffset
argument_list|()
operator|<
literal|0
condition|)
block|{
return|return;
comment|// no offsets, null snippet
block|}
name|termList
operator|.
name|add
argument_list|(
operator|new
name|TermInfo
argument_list|(
name|term
argument_list|,
name|dpEnum
operator|.
name|startOffset
argument_list|()
argument_list|,
name|dpEnum
operator|.
name|endOffset
argument_list|()
argument_list|,
name|pos
argument_list|,
name|weight
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// sort by position
name|Collections
operator|.
name|sort
argument_list|(
name|termList
argument_list|)
expr_stmt|;
comment|// now look for dups at the same position, linking them together
name|int
name|currentPos
init|=
operator|-
literal|1
decl_stmt|;
name|TermInfo
name|previous
init|=
literal|null
decl_stmt|;
name|TermInfo
name|first
init|=
literal|null
decl_stmt|;
name|Iterator
argument_list|<
name|TermInfo
argument_list|>
name|iterator
init|=
name|termList
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|TermInfo
name|current
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|current
operator|.
name|position
operator|==
name|currentPos
condition|)
block|{
assert|assert
name|previous
operator|!=
literal|null
assert|;
name|previous
operator|.
name|setNext
argument_list|(
name|current
argument_list|)
expr_stmt|;
name|previous
operator|=
name|current
expr_stmt|;
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|previous
operator|!=
literal|null
condition|)
block|{
name|previous
operator|.
name|setNext
argument_list|(
name|first
argument_list|)
expr_stmt|;
block|}
name|previous
operator|=
name|first
operator|=
name|current
expr_stmt|;
name|currentPos
operator|=
name|current
operator|.
name|position
expr_stmt|;
block|}
block|}
if|if
condition|(
name|previous
operator|!=
literal|null
condition|)
block|{
name|previous
operator|.
name|setNext
argument_list|(
name|first
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * @return field name    */
DECL|method|getFieldName
specifier|public
name|String
name|getFieldName
parameter_list|()
block|{
return|return
name|fieldName
return|;
block|}
comment|/**    * @return the top TermInfo object of the stack    */
DECL|method|pop
specifier|public
name|TermInfo
name|pop
parameter_list|()
block|{
return|return
name|termList
operator|.
name|poll
argument_list|()
return|;
block|}
comment|/**    * @param termInfo the TermInfo object to be put on the top of the stack    */
DECL|method|push
specifier|public
name|void
name|push
parameter_list|(
name|TermInfo
name|termInfo
parameter_list|)
block|{
name|termList
operator|.
name|push
argument_list|(
name|termInfo
argument_list|)
expr_stmt|;
block|}
comment|/**    * to know whether the stack is empty    *     * @return true if the stack is empty, false if not    */
DECL|method|isEmpty
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|termList
operator|==
literal|null
operator|||
name|termList
operator|.
name|size
argument_list|()
operator|==
literal|0
return|;
block|}
comment|/**    * Single term with its position/offsets in the document and IDF weight.    * It is Comparable but considers only position.    */
DECL|class|TermInfo
specifier|public
specifier|static
class|class
name|TermInfo
implements|implements
name|Comparable
argument_list|<
name|TermInfo
argument_list|>
block|{
DECL|field|text
specifier|private
specifier|final
name|String
name|text
decl_stmt|;
DECL|field|startOffset
specifier|private
specifier|final
name|int
name|startOffset
decl_stmt|;
DECL|field|endOffset
specifier|private
specifier|final
name|int
name|endOffset
decl_stmt|;
DECL|field|position
specifier|private
specifier|final
name|int
name|position
decl_stmt|;
comment|// IDF-weight of this term
DECL|field|weight
specifier|private
specifier|final
name|float
name|weight
decl_stmt|;
comment|// pointer to other TermInfo's at the same position.
comment|// this is a circular list, so with no syns, just points to itself
DECL|field|next
specifier|private
name|TermInfo
name|next
decl_stmt|;
DECL|method|TermInfo
specifier|public
name|TermInfo
parameter_list|(
name|String
name|text
parameter_list|,
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|,
name|int
name|position
parameter_list|,
name|float
name|weight
parameter_list|)
block|{
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
name|this
operator|.
name|startOffset
operator|=
name|startOffset
expr_stmt|;
name|this
operator|.
name|endOffset
operator|=
name|endOffset
expr_stmt|;
name|this
operator|.
name|position
operator|=
name|position
expr_stmt|;
name|this
operator|.
name|weight
operator|=
name|weight
expr_stmt|;
name|this
operator|.
name|next
operator|=
name|this
expr_stmt|;
block|}
DECL|method|setNext
name|void
name|setNext
parameter_list|(
name|TermInfo
name|next
parameter_list|)
block|{
name|this
operator|.
name|next
operator|=
name|next
expr_stmt|;
block|}
comment|/**       * Returns the next TermInfo at this same position.      * This is a circular list!      */
DECL|method|getNext
specifier|public
name|TermInfo
name|getNext
parameter_list|()
block|{
return|return
name|next
return|;
block|}
DECL|method|getText
specifier|public
name|String
name|getText
parameter_list|()
block|{
return|return
name|text
return|;
block|}
DECL|method|getStartOffset
specifier|public
name|int
name|getStartOffset
parameter_list|()
block|{
return|return
name|startOffset
return|;
block|}
DECL|method|getEndOffset
specifier|public
name|int
name|getEndOffset
parameter_list|()
block|{
return|return
name|endOffset
return|;
block|}
DECL|method|getPosition
specifier|public
name|int
name|getPosition
parameter_list|()
block|{
return|return
name|position
return|;
block|}
DECL|method|getWeight
specifier|public
name|float
name|getWeight
parameter_list|()
block|{
return|return
name|weight
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|text
argument_list|)
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
operator|.
name|append
argument_list|(
name|startOffset
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|endOffset
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|position
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|TermInfo
name|o
parameter_list|)
block|{
return|return
operator|(
name|this
operator|.
name|position
operator|-
name|o
operator|.
name|position
operator|)
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
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|position
expr_stmt|;
return|return
name|result
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
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|TermInfo
name|other
init|=
operator|(
name|TermInfo
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|position
operator|!=
name|other
operator|.
name|position
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
block|}
end_class
end_unit
