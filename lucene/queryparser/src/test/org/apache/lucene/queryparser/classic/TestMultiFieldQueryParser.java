begin_unit
begin_package
DECL|package|org.apache.lucene.queryparser.classic
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|classic
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
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
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
name|BooleanClause
operator|.
name|Occur
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
name|BooleanClause
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
name|BooleanQuery
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
name|RegexpQuery
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
name|ScoreDoc
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
name|IOUtils
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
begin_comment
comment|/**  * Tests QueryParser.  */
end_comment
begin_class
DECL|class|TestMultiFieldQueryParser
specifier|public
class|class
name|TestMultiFieldQueryParser
extends|extends
name|LuceneTestCase
block|{
comment|/** test stop words parsing for both the non static form, and for the     * corresponding static form (qtxt, fields[]). */
DECL|method|testStopwordsParsing
specifier|public
name|void
name|testStopwordsParsing
parameter_list|()
throws|throws
name|Exception
block|{
name|assertStopQueryEquals
argument_list|(
literal|"one"
argument_list|,
literal|"b:one t:one"
argument_list|)
expr_stmt|;
name|assertStopQueryEquals
argument_list|(
literal|"one stop"
argument_list|,
literal|"b:one t:one"
argument_list|)
expr_stmt|;
name|assertStopQueryEquals
argument_list|(
literal|"one (stop)"
argument_list|,
literal|"b:one t:one"
argument_list|)
expr_stmt|;
name|assertStopQueryEquals
argument_list|(
literal|"one ((stop))"
argument_list|,
literal|"b:one t:one"
argument_list|)
expr_stmt|;
name|assertStopQueryEquals
argument_list|(
literal|"stop"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertStopQueryEquals
argument_list|(
literal|"(stop)"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertStopQueryEquals
argument_list|(
literal|"((stop))"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
comment|// verify parsing of query using a stopping analyzer
DECL|method|assertStopQueryEquals
specifier|private
name|void
name|assertStopQueryEquals
parameter_list|(
name|String
name|qtxt
parameter_list|,
name|String
name|expectedRes
parameter_list|)
throws|throws
name|Exception
block|{
name|String
index|[]
name|fields
init|=
block|{
literal|"b"
block|,
literal|"t"
block|}
decl_stmt|;
name|Occur
name|occur
index|[]
init|=
block|{
name|Occur
operator|.
name|SHOULD
block|,
name|Occur
operator|.
name|SHOULD
block|}
decl_stmt|;
name|TestQueryParser
operator|.
name|QPTestAnalyzer
name|a
init|=
operator|new
name|TestQueryParser
operator|.
name|QPTestAnalyzer
argument_list|()
decl_stmt|;
name|MultiFieldQueryParser
name|mfqp
init|=
operator|new
name|MultiFieldQueryParser
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|fields
argument_list|,
name|a
argument_list|)
decl_stmt|;
name|Query
name|q
init|=
name|mfqp
operator|.
name|parse
argument_list|(
name|qtxt
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedRes
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
name|MultiFieldQueryParser
operator|.
name|parse
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|qtxt
argument_list|,
name|fields
argument_list|,
name|occur
argument_list|,
name|a
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedRes
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|fields
init|=
block|{
literal|"b"
block|,
literal|"t"
block|}
decl_stmt|;
name|MultiFieldQueryParser
name|mfqp
init|=
operator|new
name|MultiFieldQueryParser
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|fields
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|q
init|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"one"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"b:one t:one"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"one two"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(b:one t:one) (b:two t:two)"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"+one +two"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"+(b:one t:one) +(b:two t:two)"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"+one -two -three"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"+(b:one t:one) -(b:two t:two) -(b:three t:three)"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"one^2 two"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"((b:one t:one)^2.0) (b:two t:two)"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"one~ two"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(b:one~2 t:one~2) (b:two t:two)"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"one~0.8 two^2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(b:one~0 t:one~0) ((b:two t:two)^2.0)"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"one* two*"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(b:one* t:one*) (b:two* t:two*)"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"[a TO c] two"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(b:[a TO c] t:[a TO c]) (b:two t:two)"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"w?ldcard"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b:w?ldcard t:w?ldcard"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"\"foo bar\""
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b:\"foo bar\" t:\"foo bar\""
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"\"aa bb cc\" \"dd ee\""
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(b:\"aa bb cc\" t:\"aa bb cc\") (b:\"dd ee\" t:\"dd ee\")"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"\"foo bar\"~4"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b:\"foo bar\"~4 t:\"foo bar\"~4"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// LUCENE-1213: MultiFieldQueryParser was ignoring slop when phrase had a field.
name|q
operator|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"b:\"foo bar\"~4"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b:\"foo bar\"~4"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// make sure that terms which have a field are not touched:
name|q
operator|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"one f:two"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(b:one t:one) f:two"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// AND mode:
name|mfqp
operator|.
name|setDefaultOperator
argument_list|(
name|QueryParserBase
operator|.
name|AND_OPERATOR
argument_list|)
expr_stmt|;
name|q
operator|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"one two"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"+(b:one t:one) +(b:two t:two)"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"\"aa bb cc\" \"dd ee\""
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"+(b:\"aa bb cc\" t:\"aa bb cc\") +(b:\"dd ee\" t:\"dd ee\")"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testBoostsSimple
specifier|public
name|void
name|testBoostsSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|boosts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|boosts
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
name|Float
operator|.
name|valueOf
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|boosts
operator|.
name|put
argument_list|(
literal|"t"
argument_list|,
name|Float
operator|.
name|valueOf
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|fields
init|=
block|{
literal|"b"
block|,
literal|"t"
block|}
decl_stmt|;
name|MultiFieldQueryParser
name|mfqp
init|=
operator|new
name|MultiFieldQueryParser
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|fields
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
name|boosts
argument_list|)
decl_stmt|;
comment|//Check for simple
name|Query
name|q
init|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"one"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"b:one^5.0 t:one^10.0"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|//Check for AND
name|q
operator|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"one AND two"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"+(b:one^5.0 t:one^10.0) +(b:two^5.0 t:two^10.0)"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|//Check for OR
name|q
operator|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"one OR two"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(b:one^5.0 t:one^10.0) (b:two^5.0 t:two^10.0)"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|//Check for AND and a field
name|q
operator|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"one AND two AND foo:test"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"+(b:one^5.0 t:one^10.0) +(b:two^5.0 t:two^10.0) +foo:test"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"one^3 AND two^4"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"+((b:one^5.0 t:one^10.0)^3.0) +((b:two^5.0 t:two^10.0)^4.0)"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testStaticMethod1
specifier|public
name|void
name|testStaticMethod1
parameter_list|()
throws|throws
name|ParseException
block|{
name|String
index|[]
name|fields
init|=
block|{
literal|"b"
block|,
literal|"t"
block|}
decl_stmt|;
name|String
index|[]
name|queries
init|=
block|{
literal|"one"
block|,
literal|"two"
block|}
decl_stmt|;
name|Query
name|q
init|=
name|MultiFieldQueryParser
operator|.
name|parse
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|queries
argument_list|,
name|fields
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"b:one t:two"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|queries2
init|=
block|{
literal|"+one"
block|,
literal|"+two"
block|}
decl_stmt|;
name|q
operator|=
name|MultiFieldQueryParser
operator|.
name|parse
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|queries2
argument_list|,
name|fields
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(+b:one) (+t:two)"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|queries3
init|=
block|{
literal|"one"
block|,
literal|"+two"
block|}
decl_stmt|;
name|q
operator|=
name|MultiFieldQueryParser
operator|.
name|parse
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|queries3
argument_list|,
name|fields
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b:one (+t:two)"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|queries4
init|=
block|{
literal|"one +more"
block|,
literal|"+two"
block|}
decl_stmt|;
name|q
operator|=
name|MultiFieldQueryParser
operator|.
name|parse
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|queries4
argument_list|,
name|fields
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(b:one +b:more) (+t:two)"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|queries5
init|=
block|{
literal|"blah"
block|}
decl_stmt|;
try|try
block|{
name|q
operator|=
name|MultiFieldQueryParser
operator|.
name|parse
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|queries5
argument_list|,
name|fields
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected exception, array length differs
block|}
comment|// check also with stop words for this static form (qtxts[], fields[]).
name|TestQueryParser
operator|.
name|QPTestAnalyzer
name|stopA
init|=
operator|new
name|TestQueryParser
operator|.
name|QPTestAnalyzer
argument_list|()
decl_stmt|;
name|String
index|[]
name|queries6
init|=
block|{
literal|"((+stop))"
block|,
literal|"+((stop))"
block|}
decl_stmt|;
name|q
operator|=
name|MultiFieldQueryParser
operator|.
name|parse
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|queries6
argument_list|,
name|fields
argument_list|,
name|stopA
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|queries7
init|=
block|{
literal|"one ((+stop)) +more"
block|,
literal|"+((stop)) +two"
block|}
decl_stmt|;
name|q
operator|=
name|MultiFieldQueryParser
operator|.
name|parse
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|queries7
argument_list|,
name|fields
argument_list|,
name|stopA
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(b:one +b:more) (+t:two)"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testStaticMethod2
specifier|public
name|void
name|testStaticMethod2
parameter_list|()
throws|throws
name|ParseException
block|{
name|String
index|[]
name|fields
init|=
block|{
literal|"b"
block|,
literal|"t"
block|}
decl_stmt|;
name|BooleanClause
operator|.
name|Occur
index|[]
name|flags
init|=
block|{
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
block|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
block|}
decl_stmt|;
name|Query
name|q
init|=
name|MultiFieldQueryParser
operator|.
name|parse
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
literal|"one"
argument_list|,
name|fields
argument_list|,
name|flags
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"+b:one -t:one"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
name|MultiFieldQueryParser
operator|.
name|parse
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
literal|"one two"
argument_list|,
name|fields
argument_list|,
name|flags
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"+(b:one b:two) -(t:one t:two)"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|BooleanClause
operator|.
name|Occur
index|[]
name|flags2
init|=
block|{
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
block|}
decl_stmt|;
name|q
operator|=
name|MultiFieldQueryParser
operator|.
name|parse
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
literal|"blah"
argument_list|,
name|fields
argument_list|,
name|flags2
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected exception, array length differs
block|}
block|}
DECL|method|testStaticMethod2Old
specifier|public
name|void
name|testStaticMethod2Old
parameter_list|()
throws|throws
name|ParseException
block|{
name|String
index|[]
name|fields
init|=
block|{
literal|"b"
block|,
literal|"t"
block|}
decl_stmt|;
comment|//int[] flags = {MultiFieldQueryParser.REQUIRED_FIELD, MultiFieldQueryParser.PROHIBITED_FIELD};
name|BooleanClause
operator|.
name|Occur
index|[]
name|flags
init|=
block|{
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
block|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
block|}
decl_stmt|;
name|Query
name|q
init|=
name|MultiFieldQueryParser
operator|.
name|parse
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
literal|"one"
argument_list|,
name|fields
argument_list|,
name|flags
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|//, fields, flags, new MockAnalyzer(random));
name|assertEquals
argument_list|(
literal|"+b:one -t:one"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
name|MultiFieldQueryParser
operator|.
name|parse
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
literal|"one two"
argument_list|,
name|fields
argument_list|,
name|flags
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"+(b:one b:two) -(t:one t:two)"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|BooleanClause
operator|.
name|Occur
index|[]
name|flags2
init|=
block|{
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
block|}
decl_stmt|;
name|q
operator|=
name|MultiFieldQueryParser
operator|.
name|parse
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
literal|"blah"
argument_list|,
name|fields
argument_list|,
name|flags2
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected exception, array length differs
block|}
block|}
DECL|method|testStaticMethod3
specifier|public
name|void
name|testStaticMethod3
parameter_list|()
throws|throws
name|ParseException
block|{
name|String
index|[]
name|queries
init|=
block|{
literal|"one"
block|,
literal|"two"
block|,
literal|"three"
block|}
decl_stmt|;
name|String
index|[]
name|fields
init|=
block|{
literal|"f1"
block|,
literal|"f2"
block|,
literal|"f3"
block|}
decl_stmt|;
name|BooleanClause
operator|.
name|Occur
index|[]
name|flags
init|=
block|{
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
block|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
block|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
block|}
decl_stmt|;
name|Query
name|q
init|=
name|MultiFieldQueryParser
operator|.
name|parse
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|queries
argument_list|,
name|fields
argument_list|,
name|flags
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"+f1:one -f2:two f3:three"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|BooleanClause
operator|.
name|Occur
index|[]
name|flags2
init|=
block|{
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
block|}
decl_stmt|;
name|q
operator|=
name|MultiFieldQueryParser
operator|.
name|parse
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|queries
argument_list|,
name|fields
argument_list|,
name|flags2
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected exception, array length differs
block|}
block|}
DECL|method|testStaticMethod3Old
specifier|public
name|void
name|testStaticMethod3Old
parameter_list|()
throws|throws
name|ParseException
block|{
name|String
index|[]
name|queries
init|=
block|{
literal|"one"
block|,
literal|"two"
block|}
decl_stmt|;
name|String
index|[]
name|fields
init|=
block|{
literal|"b"
block|,
literal|"t"
block|}
decl_stmt|;
name|BooleanClause
operator|.
name|Occur
index|[]
name|flags
init|=
block|{
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
block|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
block|}
decl_stmt|;
name|Query
name|q
init|=
name|MultiFieldQueryParser
operator|.
name|parse
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|queries
argument_list|,
name|fields
argument_list|,
name|flags
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"+b:one -t:two"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|BooleanClause
operator|.
name|Occur
index|[]
name|flags2
init|=
block|{
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
block|}
decl_stmt|;
name|q
operator|=
name|MultiFieldQueryParser
operator|.
name|parse
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|queries
argument_list|,
name|fields
argument_list|,
name|flags2
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected exception, array length differs
block|}
block|}
DECL|method|testAnalyzerReturningNull
specifier|public
name|void
name|testAnalyzerReturningNull
parameter_list|()
throws|throws
name|ParseException
block|{
name|String
index|[]
name|fields
init|=
operator|new
name|String
index|[]
block|{
literal|"f1"
block|,
literal|"f2"
block|,
literal|"f3"
block|}
decl_stmt|;
name|MultiFieldQueryParser
name|parser
init|=
operator|new
name|MultiFieldQueryParser
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|fields
argument_list|,
operator|new
name|AnalyzerReturningNull
argument_list|()
argument_list|)
decl_stmt|;
name|Query
name|q
init|=
name|parser
operator|.
name|parse
argument_list|(
literal|"bla AND blo"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"+(f2:bla f3:bla) +(f2:blo f3:blo)"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// the following queries are not affected as their terms are not analyzed anyway:
name|q
operator|=
name|parser
operator|.
name|parse
argument_list|(
literal|"bla*"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"f1:bla* f2:bla* f3:bla*"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
name|parser
operator|.
name|parse
argument_list|(
literal|"bla~"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"f1:bla~2 f2:bla~2 f3:bla~2"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
name|parser
operator|.
name|parse
argument_list|(
literal|"[a TO c]"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"f1:[a TO c] f2:[a TO c] f3:[a TO c]"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testStopWordSearching
specifier|public
name|void
name|testStopWordSearching
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|Directory
name|ramDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|ramDir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|analyzer
argument_list|)
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
name|newTextField
argument_list|(
literal|"body"
argument_list|,
literal|"blah the footest blah"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|iw
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|MultiFieldQueryParser
name|mfqp
init|=
operator|new
name|MultiFieldQueryParser
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"body"
block|}
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|mfqp
operator|.
name|setDefaultOperator
argument_list|(
name|QueryParser
operator|.
name|Operator
operator|.
name|AND
argument_list|)
expr_stmt|;
name|Query
name|q
init|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"the footest"
argument_list|)
decl_stmt|;
name|IndexReader
name|ir
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|ramDir
argument_list|)
decl_stmt|;
name|IndexSearcher
name|is
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|is
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|ramDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Return no tokens for field "f1".    */
DECL|class|AnalyzerReturningNull
specifier|private
specifier|static
class|class
name|AnalyzerReturningNull
extends|extends
name|Analyzer
block|{
DECL|field|stdAnalyzer
name|MockAnalyzer
name|stdAnalyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|AnalyzerReturningNull
specifier|public
name|AnalyzerReturningNull
parameter_list|()
block|{
name|super
argument_list|(
name|PER_FIELD_REUSE_STRATEGY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|initReader
specifier|protected
name|Reader
name|initReader
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
if|if
condition|(
literal|"f1"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
comment|// we don't use the reader, so close it:
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|reader
argument_list|)
expr_stmt|;
comment|// return empty reader, so MockTokenizer returns no tokens:
return|return
operator|new
name|StringReader
argument_list|(
literal|""
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|initReader
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|createComponents
specifier|public
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|stdAnalyzer
operator|.
name|createComponents
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
block|}
DECL|method|testSimpleRegex
specifier|public
name|void
name|testSimpleRegex
parameter_list|()
throws|throws
name|ParseException
block|{
name|String
index|[]
name|fields
init|=
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|}
decl_stmt|;
name|MultiFieldQueryParser
name|mfqp
init|=
operator|new
name|MultiFieldQueryParser
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|fields
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|RegexpQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"a"
argument_list|,
literal|"[a-z][123]"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|RegexpQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"b"
argument_list|,
literal|"[a-z][123]"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|bq
argument_list|,
name|mfqp
operator|.
name|parse
argument_list|(
literal|"/[a-z][123]/"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
