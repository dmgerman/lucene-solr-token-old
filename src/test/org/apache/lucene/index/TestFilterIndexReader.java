begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package
begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001, 2002, 2003 The Apache Software Foundation.  * All rights reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestSuite
import|;
end_import
begin_import
import|import
name|junit
operator|.
name|textui
operator|.
name|TestRunner
import|;
end_import
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestResult
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
name|Searcher
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
name|Hits
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
name|TermQuery
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
name|store
operator|.
name|RAMDirectory
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
name|FSDirectory
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
name|standard
operator|.
name|StandardAnalyzer
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
name|WhitespaceAnalyzer
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
name|java
operator|.
name|util
operator|.
name|Collection
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
begin_class
DECL|class|TestFilterIndexReader
specifier|public
class|class
name|TestFilterIndexReader
extends|extends
name|TestCase
block|{
DECL|class|TestReader
specifier|private
specifier|static
class|class
name|TestReader
extends|extends
name|FilterIndexReader
block|{
comment|/** Filter that only permits terms containing 'e'.*/
DECL|class|TestTermEnum
specifier|private
specifier|static
class|class
name|TestTermEnum
extends|extends
name|FilterTermEnum
block|{
DECL|method|TestTermEnum
specifier|public
name|TestTermEnum
parameter_list|(
name|TermEnum
name|enum
constructor|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
expr|enum
argument_list|)
empty_stmt|;
block|}
comment|/** Scan for terms containing the letter 'e'.*/
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
name|in
operator|.
name|next
argument_list|()
condition|)
block|{
if|if
condition|(
name|in
operator|.
name|term
argument_list|()
operator|.
name|text
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|'e'
argument_list|)
operator|!=
operator|-
literal|1
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
comment|/** Filter that only returns odd numbered documents. */
DECL|class|TestTermPositions
specifier|private
specifier|static
class|class
name|TestTermPositions
extends|extends
name|FilterTermPositions
block|{
DECL|method|TestTermPositions
specifier|public
name|TestTermPositions
parameter_list|(
name|TermPositions
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
comment|/** Scan for odd numbered documents. */
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
name|in
operator|.
name|next
argument_list|()
condition|)
block|{
if|if
condition|(
operator|(
name|in
operator|.
name|doc
argument_list|()
operator|%
literal|2
operator|)
operator|==
literal|1
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
DECL|method|TestReader
specifier|public
name|TestReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
name|super
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
comment|/** Filter terms with TestTermEnum. */
DECL|method|terms
specifier|public
name|TermEnum
name|terms
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|TestTermEnum
argument_list|(
name|in
operator|.
name|terms
argument_list|()
argument_list|)
return|;
block|}
comment|/** Filter positions with TestTermPositions. */
DECL|method|termPositions
specifier|public
name|TermPositions
name|termPositions
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|TestTermPositions
argument_list|(
name|in
operator|.
name|termPositions
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class
begin_comment
comment|/** Main for running test case by itself. */
end_comment
begin_function
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
block|{
name|TestRunner
operator|.
name|run
argument_list|(
operator|new
name|TestSuite
argument_list|(
name|TestIndexReader
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function
begin_comment
comment|/**    * Tests the IndexReader.getFieldNames implementation    * @throws Exception on error    */
end_comment
begin_function
DECL|method|testFilterIndexReader
specifier|public
name|void
name|testFilterIndexReader
parameter_list|()
throws|throws
name|Exception
block|{
name|RAMDirectory
name|directory
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Document
name|d1
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d1
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Text
argument_list|(
literal|"default"
argument_list|,
literal|"one two"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d1
argument_list|)
expr_stmt|;
name|Document
name|d2
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d2
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Text
argument_list|(
literal|"default"
argument_list|,
literal|"one three"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d2
argument_list|)
expr_stmt|;
name|Document
name|d3
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d3
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Text
argument_list|(
literal|"default"
argument_list|,
literal|"two four"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d3
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
operator|new
name|TestReader
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
argument_list|)
decl_stmt|;
name|TermEnum
name|terms
init|=
name|reader
operator|.
name|terms
argument_list|()
decl_stmt|;
while|while
condition|(
name|terms
operator|.
name|next
argument_list|()
condition|)
block|{
name|assertTrue
argument_list|(
name|terms
operator|.
name|term
argument_list|()
operator|.
name|text
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|'e'
argument_list|)
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|terms
operator|.
name|close
argument_list|()
expr_stmt|;
name|TermPositions
name|positions
init|=
name|reader
operator|.
name|termPositions
argument_list|(
operator|new
name|Term
argument_list|(
literal|"default"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
name|positions
operator|.
name|next
argument_list|()
condition|)
block|{
name|assertTrue
argument_list|(
operator|(
name|positions
operator|.
name|doc
argument_list|()
operator|%
literal|2
operator|)
operator|==
literal|1
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
end_function
unit|}
end_unit
