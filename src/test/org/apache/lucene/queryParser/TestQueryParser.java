begin_unit
begin_package
DECL|package|org.apache.lucene.queryParser
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
package|;
end_package
begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001, 2002, 2003 The Apache Software Foundation.  All  * rights reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|*
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
name|*
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
name|queryParser
operator|.
name|*
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
name|*
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
name|DateField
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
name|*
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
name|*
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
begin_comment
comment|/**  * Tests QueryParser.  *  * @version $Id$  */
end_comment
begin_class
DECL|class|TestQueryParser
specifier|public
class|class
name|TestQueryParser
extends|extends
name|TestCase
block|{
DECL|method|TestQueryParser
specifier|public
name|TestQueryParser
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|field|qpAnalyzer
specifier|public
specifier|static
name|Analyzer
name|qpAnalyzer
init|=
operator|new
name|QPTestAnalyzer
argument_list|()
decl_stmt|;
DECL|class|QPTestFilter
specifier|public
specifier|static
class|class
name|QPTestFilter
extends|extends
name|TokenFilter
block|{
comment|/** 	 * Filter which discards the token 'stop' and which expands the 	 * token 'phrase' into 'phrase1 phrase2' 	 */
DECL|method|QPTestFilter
specifier|public
name|QPTestFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
DECL|field|inPhrase
name|boolean
name|inPhrase
init|=
literal|false
decl_stmt|;
DECL|field|savedStart
DECL|field|savedEnd
name|int
name|savedStart
init|=
literal|0
decl_stmt|,
name|savedEnd
init|=
literal|0
decl_stmt|;
DECL|method|next
specifier|public
name|Token
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|inPhrase
condition|)
block|{
name|inPhrase
operator|=
literal|false
expr_stmt|;
return|return
operator|new
name|Token
argument_list|(
literal|"phrase2"
argument_list|,
name|savedStart
argument_list|,
name|savedEnd
argument_list|)
return|;
block|}
else|else
for|for
control|(
name|Token
name|token
init|=
name|input
operator|.
name|next
argument_list|()
init|;
name|token
operator|!=
literal|null
condition|;
name|token
operator|=
name|input
operator|.
name|next
argument_list|()
control|)
block|{
if|if
condition|(
name|token
operator|.
name|termText
argument_list|()
operator|.
name|equals
argument_list|(
literal|"phrase"
argument_list|)
condition|)
block|{
name|inPhrase
operator|=
literal|true
expr_stmt|;
name|savedStart
operator|=
name|token
operator|.
name|startOffset
argument_list|()
expr_stmt|;
name|savedEnd
operator|=
name|token
operator|.
name|endOffset
argument_list|()
expr_stmt|;
return|return
operator|new
name|Token
argument_list|(
literal|"phrase1"
argument_list|,
name|savedStart
argument_list|,
name|savedEnd
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|token
operator|.
name|termText
argument_list|()
operator|.
name|equals
argument_list|(
literal|"stop"
argument_list|)
condition|)
return|return
name|token
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
DECL|class|QPTestAnalyzer
specifier|public
specifier|static
class|class
name|QPTestAnalyzer
extends|extends
name|Analyzer
block|{
DECL|method|QPTestAnalyzer
specifier|public
name|QPTestAnalyzer
parameter_list|()
block|{ 	}
comment|/** Filters LowerCaseTokenizer with StopFilter. */
DECL|method|tokenStream
specifier|public
specifier|final
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|QPTestFilter
argument_list|(
operator|new
name|LowerCaseTokenizer
argument_list|(
name|reader
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|method|getParser
specifier|public
name|QueryParser
name|getParser
parameter_list|(
name|Analyzer
name|a
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|a
operator|==
literal|null
condition|)
name|a
operator|=
operator|new
name|SimpleAnalyzer
argument_list|()
expr_stmt|;
name|QueryParser
name|qp
init|=
operator|new
name|QueryParser
argument_list|(
literal|"field"
argument_list|,
name|a
argument_list|)
decl_stmt|;
name|qp
operator|.
name|setOperator
argument_list|(
name|QueryParser
operator|.
name|DEFAULT_OPERATOR_OR
argument_list|)
expr_stmt|;
return|return
name|qp
return|;
block|}
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|(
name|String
name|query
parameter_list|,
name|Analyzer
name|a
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|getParser
argument_list|(
name|a
argument_list|)
operator|.
name|parse
argument_list|(
name|query
argument_list|)
return|;
block|}
DECL|method|assertQueryEquals
specifier|public
name|void
name|assertQueryEquals
parameter_list|(
name|String
name|query
parameter_list|,
name|Analyzer
name|a
parameter_list|,
name|String
name|result
parameter_list|)
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
name|getQuery
argument_list|(
name|query
argument_list|,
name|a
argument_list|)
decl_stmt|;
name|String
name|s
init|=
name|q
operator|.
name|toString
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|s
operator|.
name|equals
argument_list|(
name|result
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Query /"
operator|+
name|query
operator|+
literal|"/ yielded /"
operator|+
name|s
operator|+
literal|"/, expecting /"
operator|+
name|result
operator|+
literal|"/"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertWildcardQueryEquals
specifier|public
name|void
name|assertWildcardQueryEquals
parameter_list|(
name|String
name|query
parameter_list|,
name|boolean
name|lowercase
parameter_list|,
name|String
name|result
parameter_list|)
throws|throws
name|Exception
block|{
name|QueryParser
name|qp
init|=
name|getParser
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|qp
operator|.
name|setLowercaseWildcardTerms
argument_list|(
name|lowercase
argument_list|)
expr_stmt|;
name|Query
name|q
init|=
name|qp
operator|.
name|parse
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|String
name|s
init|=
name|q
operator|.
name|toString
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|s
operator|.
name|equals
argument_list|(
name|result
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"WildcardQuery /"
operator|+
name|query
operator|+
literal|"/ yielded /"
operator|+
name|s
operator|+
literal|"/, expecting /"
operator|+
name|result
operator|+
literal|"/"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getQueryDOA
specifier|public
name|Query
name|getQueryDOA
parameter_list|(
name|String
name|query
parameter_list|,
name|Analyzer
name|a
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|a
operator|==
literal|null
condition|)
name|a
operator|=
operator|new
name|SimpleAnalyzer
argument_list|()
expr_stmt|;
name|QueryParser
name|qp
init|=
operator|new
name|QueryParser
argument_list|(
literal|"field"
argument_list|,
name|a
argument_list|)
decl_stmt|;
name|qp
operator|.
name|setOperator
argument_list|(
name|QueryParser
operator|.
name|DEFAULT_OPERATOR_AND
argument_list|)
expr_stmt|;
return|return
name|qp
operator|.
name|parse
argument_list|(
name|query
argument_list|)
return|;
block|}
DECL|method|assertQueryEqualsDOA
specifier|public
name|void
name|assertQueryEqualsDOA
parameter_list|(
name|String
name|query
parameter_list|,
name|Analyzer
name|a
parameter_list|,
name|String
name|result
parameter_list|)
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
name|getQueryDOA
argument_list|(
name|query
argument_list|,
name|a
argument_list|)
decl_stmt|;
name|String
name|s
init|=
name|q
operator|.
name|toString
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|s
operator|.
name|equals
argument_list|(
name|result
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Query /"
operator|+
name|query
operator|+
literal|"/ yielded /"
operator|+
name|s
operator|+
literal|"/, expecting /"
operator|+
name|result
operator|+
literal|"/"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQueryEquals
argument_list|(
literal|"term term term"
argument_list|,
literal|null
argument_list|,
literal|"term term term"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"türm term term"
argument_list|,
literal|null
argument_list|,
literal|"türm term term"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"ümlaut"
argument_list|,
literal|null
argument_list|,
literal|"ümlaut"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"a AND b"
argument_list|,
literal|null
argument_list|,
literal|"+a +b"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"(a AND b)"
argument_list|,
literal|null
argument_list|,
literal|"+a +b"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"c OR (a AND b)"
argument_list|,
literal|null
argument_list|,
literal|"c (+a +b)"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"a AND NOT b"
argument_list|,
literal|null
argument_list|,
literal|"+a -b"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"a AND -b"
argument_list|,
literal|null
argument_list|,
literal|"+a -b"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"a AND !b"
argument_list|,
literal|null
argument_list|,
literal|"+a -b"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"a&& b"
argument_list|,
literal|null
argument_list|,
literal|"+a +b"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"a&& ! b"
argument_list|,
literal|null
argument_list|,
literal|"+a -b"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"a OR b"
argument_list|,
literal|null
argument_list|,
literal|"a b"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"a || b"
argument_list|,
literal|null
argument_list|,
literal|"a b"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"a OR !b"
argument_list|,
literal|null
argument_list|,
literal|"a -b"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"a OR ! b"
argument_list|,
literal|null
argument_list|,
literal|"a -b"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"a OR -b"
argument_list|,
literal|null
argument_list|,
literal|"a -b"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"+term -term term"
argument_list|,
literal|null
argument_list|,
literal|"+term -term term"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"foo:term AND field:anotherTerm"
argument_list|,
literal|null
argument_list|,
literal|"+foo:term +anotherterm"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"term AND \"phrase phrase\""
argument_list|,
literal|null
argument_list|,
literal|"+term +\"phrase phrase\""
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"\"hello there\""
argument_list|,
literal|null
argument_list|,
literal|"\"hello there\""
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getQuery
argument_list|(
literal|"a AND b"
argument_list|,
literal|null
argument_list|)
operator|instanceof
name|BooleanQuery
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getQuery
argument_list|(
literal|"hello"
argument_list|,
literal|null
argument_list|)
operator|instanceof
name|TermQuery
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getQuery
argument_list|(
literal|"\"hello there\""
argument_list|,
literal|null
argument_list|)
operator|instanceof
name|PhraseQuery
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"germ term^2.0"
argument_list|,
literal|null
argument_list|,
literal|"germ term^2.0"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"(term)^2.0"
argument_list|,
literal|null
argument_list|,
literal|"term^2.0"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"(germ term)^2.0"
argument_list|,
literal|null
argument_list|,
literal|"(germ term)^2.0"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"term^2.0"
argument_list|,
literal|null
argument_list|,
literal|"term^2.0"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"term^2"
argument_list|,
literal|null
argument_list|,
literal|"term^2.0"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"\"germ term\"^2.0"
argument_list|,
literal|null
argument_list|,
literal|"\"germ term\"^2.0"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"\"term germ\"^2"
argument_list|,
literal|null
argument_list|,
literal|"\"term germ\"^2.0"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"(foo OR bar) AND (baz OR boo)"
argument_list|,
literal|null
argument_list|,
literal|"+(foo bar) +(baz boo)"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"((a OR b) AND NOT c) OR d"
argument_list|,
literal|null
argument_list|,
literal|"(+(a b) -c) d"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"+(apple \"steve jobs\") -(foo bar baz)"
argument_list|,
literal|null
argument_list|,
literal|"+(apple \"steve jobs\") -(foo bar baz)"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"+title:(dog OR cat) -author:\"bob dole\""
argument_list|,
literal|null
argument_list|,
literal|"+(title:dog title:cat) -author:\"bob dole\""
argument_list|)
expr_stmt|;
block|}
DECL|method|testPunct
specifier|public
name|void
name|testPunct
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|WhitespaceAnalyzer
argument_list|()
decl_stmt|;
name|assertQueryEquals
argument_list|(
literal|"a&b"
argument_list|,
name|a
argument_list|,
literal|"a&b"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"a&&b"
argument_list|,
name|a
argument_list|,
literal|"a&&b"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|".NET"
argument_list|,
name|a
argument_list|,
literal|".NET"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSlop
specifier|public
name|void
name|testSlop
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQueryEquals
argument_list|(
literal|"\"term germ\"~2"
argument_list|,
literal|null
argument_list|,
literal|"\"term germ\"~2"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"\"term germ\"~2 flork"
argument_list|,
literal|null
argument_list|,
literal|"\"term germ\"~2 flork"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"\"term\"~2"
argument_list|,
literal|null
argument_list|,
literal|"term"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"\" \"~2 germ"
argument_list|,
literal|null
argument_list|,
literal|"germ"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"\"term germ\"~2^2"
argument_list|,
literal|null
argument_list|,
literal|"\"term germ\"~2^2.0"
argument_list|)
expr_stmt|;
block|}
DECL|method|testNumber
specifier|public
name|void
name|testNumber
parameter_list|()
throws|throws
name|Exception
block|{
comment|// The numbers go away because SimpleAnalzyer ignores them
name|assertQueryEquals
argument_list|(
literal|"3"
argument_list|,
literal|null
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"term 1.0 1 2"
argument_list|,
literal|null
argument_list|,
literal|"term"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"term term1 term2"
argument_list|,
literal|null
argument_list|,
literal|"term term term"
argument_list|)
expr_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|StandardAnalyzer
argument_list|()
decl_stmt|;
name|assertQueryEquals
argument_list|(
literal|"3"
argument_list|,
name|a
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"term 1.0 1 2"
argument_list|,
name|a
argument_list|,
literal|"term 1.0 1 2"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"term term1 term2"
argument_list|,
name|a
argument_list|,
literal|"term term1 term2"
argument_list|)
expr_stmt|;
block|}
DECL|method|testWildcard
specifier|public
name|void
name|testWildcard
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQueryEquals
argument_list|(
literal|"term*"
argument_list|,
literal|null
argument_list|,
literal|"term*"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"term*^2"
argument_list|,
literal|null
argument_list|,
literal|"term*^2.0"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"term~"
argument_list|,
literal|null
argument_list|,
literal|"term~"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"term~^2"
argument_list|,
literal|null
argument_list|,
literal|"term^2.0~"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"term^2~"
argument_list|,
literal|null
argument_list|,
literal|"term^2.0~"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"term*germ"
argument_list|,
literal|null
argument_list|,
literal|"term*germ"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"term*germ^3"
argument_list|,
literal|null
argument_list|,
literal|"term*germ^3.0"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getQuery
argument_list|(
literal|"term*"
argument_list|,
literal|null
argument_list|)
operator|instanceof
name|PrefixQuery
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getQuery
argument_list|(
literal|"term*^2"
argument_list|,
literal|null
argument_list|)
operator|instanceof
name|PrefixQuery
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getQuery
argument_list|(
literal|"term~"
argument_list|,
literal|null
argument_list|)
operator|instanceof
name|FuzzyQuery
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getQuery
argument_list|(
literal|"term*germ"
argument_list|,
literal|null
argument_list|)
operator|instanceof
name|WildcardQuery
argument_list|)
expr_stmt|;
comment|/* Tests to see that wild card terms are (or are not) properly 	 * lower-cased with propery parser configuration 	 */
comment|// First prefix queries:
name|assertWildcardQueryEquals
argument_list|(
literal|"term*"
argument_list|,
literal|true
argument_list|,
literal|"term*"
argument_list|)
expr_stmt|;
name|assertWildcardQueryEquals
argument_list|(
literal|"Term*"
argument_list|,
literal|true
argument_list|,
literal|"term*"
argument_list|)
expr_stmt|;
name|assertWildcardQueryEquals
argument_list|(
literal|"TERM*"
argument_list|,
literal|true
argument_list|,
literal|"term*"
argument_list|)
expr_stmt|;
name|assertWildcardQueryEquals
argument_list|(
literal|"term*"
argument_list|,
literal|false
argument_list|,
literal|"term*"
argument_list|)
expr_stmt|;
name|assertWildcardQueryEquals
argument_list|(
literal|"Term*"
argument_list|,
literal|false
argument_list|,
literal|"Term*"
argument_list|)
expr_stmt|;
name|assertWildcardQueryEquals
argument_list|(
literal|"TERM*"
argument_list|,
literal|false
argument_list|,
literal|"TERM*"
argument_list|)
expr_stmt|;
comment|// Then 'full' wildcard queries:
name|assertWildcardQueryEquals
argument_list|(
literal|"te?m"
argument_list|,
literal|true
argument_list|,
literal|"te?m"
argument_list|)
expr_stmt|;
name|assertWildcardQueryEquals
argument_list|(
literal|"Te?m"
argument_list|,
literal|true
argument_list|,
literal|"te?m"
argument_list|)
expr_stmt|;
name|assertWildcardQueryEquals
argument_list|(
literal|"TE?M"
argument_list|,
literal|true
argument_list|,
literal|"te?m"
argument_list|)
expr_stmt|;
name|assertWildcardQueryEquals
argument_list|(
literal|"Te?m*gerM"
argument_list|,
literal|true
argument_list|,
literal|"te?m*germ"
argument_list|)
expr_stmt|;
name|assertWildcardQueryEquals
argument_list|(
literal|"te?m"
argument_list|,
literal|false
argument_list|,
literal|"te?m"
argument_list|)
expr_stmt|;
name|assertWildcardQueryEquals
argument_list|(
literal|"Te?m"
argument_list|,
literal|false
argument_list|,
literal|"Te?m"
argument_list|)
expr_stmt|;
name|assertWildcardQueryEquals
argument_list|(
literal|"TE?M"
argument_list|,
literal|false
argument_list|,
literal|"TE?M"
argument_list|)
expr_stmt|;
name|assertWildcardQueryEquals
argument_list|(
literal|"Te?m*gerM"
argument_list|,
literal|false
argument_list|,
literal|"Te?m*gerM"
argument_list|)
expr_stmt|;
block|}
DECL|method|testQPA
specifier|public
name|void
name|testQPA
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQueryEquals
argument_list|(
literal|"term term term"
argument_list|,
name|qpAnalyzer
argument_list|,
literal|"term term term"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"term +stop term"
argument_list|,
name|qpAnalyzer
argument_list|,
literal|"term term"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"term -stop term"
argument_list|,
name|qpAnalyzer
argument_list|,
literal|"term term"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"drop AND stop AND roll"
argument_list|,
name|qpAnalyzer
argument_list|,
literal|"+drop +roll"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"term phrase term"
argument_list|,
name|qpAnalyzer
argument_list|,
literal|"term \"phrase1 phrase2\" term"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"term AND NOT phrase term"
argument_list|,
name|qpAnalyzer
argument_list|,
literal|"+term -\"phrase1 phrase2\" term"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"stop"
argument_list|,
name|qpAnalyzer
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getQuery
argument_list|(
literal|"term term term"
argument_list|,
name|qpAnalyzer
argument_list|)
operator|instanceof
name|BooleanQuery
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getQuery
argument_list|(
literal|"term +stop"
argument_list|,
name|qpAnalyzer
argument_list|)
operator|instanceof
name|TermQuery
argument_list|)
expr_stmt|;
block|}
DECL|method|testRange
specifier|public
name|void
name|testRange
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQueryEquals
argument_list|(
literal|"[ a TO z]"
argument_list|,
literal|null
argument_list|,
literal|"[a-z]"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getQuery
argument_list|(
literal|"[ a TO z]"
argument_list|,
literal|null
argument_list|)
operator|instanceof
name|RangeQuery
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"[ a TO z ]"
argument_list|,
literal|null
argument_list|,
literal|"[a-z]"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"{ a TO z}"
argument_list|,
literal|null
argument_list|,
literal|"{a-z}"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"{ a TO z }"
argument_list|,
literal|null
argument_list|,
literal|"{a-z}"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"{ a TO z }^2.0"
argument_list|,
literal|null
argument_list|,
literal|"{a-z}^2.0"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"[ a TO z] OR bar"
argument_list|,
literal|null
argument_list|,
literal|"[a-z] bar"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"[ a TO z] AND bar"
argument_list|,
literal|null
argument_list|,
literal|"+[a-z] +bar"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"( bar blar { a TO z}) "
argument_list|,
literal|null
argument_list|,
literal|"bar blar {a-z}"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"gack ( bar blar { a TO z}) "
argument_list|,
literal|null
argument_list|,
literal|"gack (bar blar {a-z})"
argument_list|)
expr_stmt|;
block|}
DECL|method|getDate
specifier|public
name|String
name|getDate
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|Exception
block|{
name|DateFormat
name|df
init|=
name|DateFormat
operator|.
name|getDateInstance
argument_list|(
name|DateFormat
operator|.
name|SHORT
argument_list|)
decl_stmt|;
return|return
name|DateField
operator|.
name|dateToString
argument_list|(
name|df
operator|.
name|parse
argument_list|(
name|s
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getLocalizedDate
specifier|public
name|String
name|getLocalizedDate
parameter_list|(
name|int
name|year
parameter_list|,
name|int
name|month
parameter_list|,
name|int
name|day
parameter_list|)
block|{
name|DateFormat
name|df
init|=
name|DateFormat
operator|.
name|getDateInstance
argument_list|(
name|DateFormat
operator|.
name|SHORT
argument_list|)
decl_stmt|;
name|Calendar
name|calendar
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|year
argument_list|,
name|month
argument_list|,
name|day
argument_list|)
expr_stmt|;
return|return
name|df
operator|.
name|format
argument_list|(
name|calendar
operator|.
name|getTime
argument_list|()
argument_list|)
return|;
block|}
DECL|method|testDateRange
specifier|public
name|void
name|testDateRange
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|startDate
init|=
name|getLocalizedDate
argument_list|(
literal|2002
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|String
name|endDate
init|=
name|getLocalizedDate
argument_list|(
literal|2002
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|assertQueryEquals
argument_list|(
literal|"[ "
operator|+
name|startDate
operator|+
literal|" TO "
operator|+
name|endDate
operator|+
literal|"]"
argument_list|,
literal|null
argument_list|,
literal|"["
operator|+
name|getDate
argument_list|(
name|startDate
argument_list|)
operator|+
literal|"-"
operator|+
name|getDate
argument_list|(
name|endDate
argument_list|)
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"{  "
operator|+
name|startDate
operator|+
literal|"    "
operator|+
name|endDate
operator|+
literal|"   }"
argument_list|,
literal|null
argument_list|,
literal|"{"
operator|+
name|getDate
argument_list|(
name|startDate
argument_list|)
operator|+
literal|"-"
operator|+
name|getDate
argument_list|(
name|endDate
argument_list|)
operator|+
literal|"}"
argument_list|)
expr_stmt|;
block|}
DECL|method|testEscaped
specifier|public
name|void
name|testEscaped
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|WhitespaceAnalyzer
argument_list|()
decl_stmt|;
name|assertQueryEquals
argument_list|(
literal|"\\[brackets"
argument_list|,
name|a
argument_list|,
literal|"\\[brackets"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"\\[brackets"
argument_list|,
literal|null
argument_list|,
literal|"brackets"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"\\\\"
argument_list|,
name|a
argument_list|,
literal|"\\\\"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"\\+blah"
argument_list|,
name|a
argument_list|,
literal|"\\+blah"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"\\(blah"
argument_list|,
name|a
argument_list|,
literal|"\\(blah"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSimpleDAO
specifier|public
name|void
name|testSimpleDAO
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQueryEqualsDOA
argument_list|(
literal|"term term term"
argument_list|,
literal|null
argument_list|,
literal|"+term +term +term"
argument_list|)
expr_stmt|;
name|assertQueryEqualsDOA
argument_list|(
literal|"term +term term"
argument_list|,
literal|null
argument_list|,
literal|"+term +term +term"
argument_list|)
expr_stmt|;
name|assertQueryEqualsDOA
argument_list|(
literal|"term term +term"
argument_list|,
literal|null
argument_list|,
literal|"+term +term +term"
argument_list|)
expr_stmt|;
name|assertQueryEqualsDOA
argument_list|(
literal|"term +term +term"
argument_list|,
literal|null
argument_list|,
literal|"+term +term +term"
argument_list|)
expr_stmt|;
name|assertQueryEqualsDOA
argument_list|(
literal|"-term term term"
argument_list|,
literal|null
argument_list|,
literal|"-term +term +term"
argument_list|)
expr_stmt|;
block|}
DECL|method|testBoost
specifier|public
name|void
name|testBoost
parameter_list|()
throws|throws
name|Exception
block|{
name|StandardAnalyzer
name|oneStopAnalyzer
init|=
operator|new
name|StandardAnalyzer
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"on"
block|}
argument_list|)
decl_stmt|;
name|QueryParser
name|qp
init|=
operator|new
name|QueryParser
argument_list|(
literal|"field"
argument_list|,
name|oneStopAnalyzer
argument_list|)
decl_stmt|;
name|Query
name|q
init|=
name|qp
operator|.
name|parse
argument_list|(
literal|"on^1.0"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|q
operator|=
name|qp
operator|.
name|parse
argument_list|(
literal|"\"hello\"^2.0"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|q
operator|.
name|getBoost
argument_list|()
argument_list|,
operator|(
name|float
operator|)
literal|2.0
argument_list|,
operator|(
name|float
operator|)
literal|0.5
argument_list|)
expr_stmt|;
name|q
operator|=
name|qp
operator|.
name|parse
argument_list|(
literal|"hello^2.0"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|q
operator|.
name|getBoost
argument_list|()
argument_list|,
operator|(
name|float
operator|)
literal|2.0
argument_list|,
operator|(
name|float
operator|)
literal|0.5
argument_list|)
expr_stmt|;
name|q
operator|=
name|qp
operator|.
name|parse
argument_list|(
literal|"\"on\"^1.0"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
