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
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
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
name|Hashtable
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
name|index
operator|.
name|IndexReader
import|;
end_import
begin_comment
comment|/** The abstract base class for queries.<p>Instantiable subclasses are:<ul><li> {@link TermQuery}<li> {@link PhraseQuery}<li> {@link BooleanQuery}</ul><p>A parser for queries is contained in:<ul><li><a href="/lucene/docs/api/org/apache/lucene/queryParser/QueryParser.html">QueryParser</a></ul>   */
end_comment
begin_class
DECL|class|Query
specifier|abstract
specifier|public
class|class
name|Query
implements|implements
name|java
operator|.
name|io
operator|.
name|Serializable
block|{
comment|// query boost factor
DECL|field|boost
specifier|protected
name|float
name|boost
init|=
literal|1.0f
decl_stmt|;
comment|// query weighting
DECL|method|sumOfSquaredWeights
specifier|abstract
name|float
name|sumOfSquaredWeights
parameter_list|(
name|Searcher
name|searcher
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|normalize
specifier|abstract
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|)
function_decl|;
comment|// query evaluation
DECL|method|scorer
specifier|abstract
name|Scorer
name|scorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|prepare
name|void
name|prepare
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{}
DECL|method|scorer
specifier|static
name|Scorer
name|scorer
parameter_list|(
name|Query
name|query
parameter_list|,
name|Searcher
name|searcher
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|query
operator|.
name|prepare
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|float
name|sum
init|=
name|query
operator|.
name|sumOfSquaredWeights
argument_list|(
name|searcher
argument_list|)
decl_stmt|;
name|float
name|norm
init|=
literal|1.0f
operator|/
operator|(
name|float
operator|)
name|Math
operator|.
name|sqrt
argument_list|(
name|sum
argument_list|)
decl_stmt|;
name|query
operator|.
name|normalize
argument_list|(
name|norm
argument_list|)
expr_stmt|;
return|return
name|query
operator|.
name|scorer
argument_list|(
name|reader
argument_list|)
return|;
block|}
comment|/** Sets the boost for this term to<code>b</code>.  Documents containing     this term will (in addition to the normal weightings) have their score     multiplied by<code>b</code>. */
DECL|method|setBoost
specifier|public
name|void
name|setBoost
parameter_list|(
name|float
name|b
parameter_list|)
block|{
name|boost
operator|=
name|b
expr_stmt|;
block|}
comment|/** Gets the boost for this term.  Documents containing     this term will (in addition to the normal weightings) have their score     multiplied by<code>b</code>.   The boost is 1.0 by default.  */
DECL|method|getBoost
specifier|public
name|float
name|getBoost
parameter_list|()
block|{
return|return
name|boost
return|;
block|}
comment|/** Prints a query to a string, with<code>field</code> as the default field     for terms.<p>The representation used is one that is readable by<a href="doc/lucene.queryParser.QueryParser.html">QueryParser</a>     (although, if the query was created by the parser, the printed     representation may not be exactly what was parsed). */
DECL|method|toString
specifier|abstract
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
function_decl|;
block|}
end_class
end_unit
