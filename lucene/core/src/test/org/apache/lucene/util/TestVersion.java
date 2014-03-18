begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package
begin_class
DECL|class|TestVersion
specifier|public
class|class
name|TestVersion
extends|extends
name|LuceneTestCase
block|{
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
block|{
for|for
control|(
name|Version
name|v
range|:
name|Version
operator|.
name|values
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
literal|"LUCENE_CURRENT must be always onOrAfter("
operator|+
name|v
operator|+
literal|")"
argument_list|,
name|Version
operator|.
name|LUCENE_CURRENT
operator|.
name|onOrAfter
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|Version
operator|.
name|LUCENE_50
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|LUCENE_40
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Version
operator|.
name|LUCENE_40
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|LUCENE_50
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testParseLeniently
specifier|public
name|void
name|testParseLeniently
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Version
operator|.
name|LUCENE_40
argument_list|,
name|Version
operator|.
name|parseLeniently
argument_list|(
literal|"4.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Version
operator|.
name|LUCENE_40
argument_list|,
name|Version
operator|.
name|parseLeniently
argument_list|(
literal|"LUCENE_40"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
name|Version
operator|.
name|parseLeniently
argument_list|(
literal|"LUCENE_CURRENT"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDeprecations
specifier|public
name|void
name|testDeprecations
parameter_list|()
throws|throws
name|Exception
block|{
name|Version
name|values
index|[]
init|=
name|Version
operator|.
name|values
argument_list|()
decl_stmt|;
comment|// all but the latest version should be deprecated
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|+
literal|1
operator|==
name|values
operator|.
name|length
condition|)
block|{
name|assertSame
argument_list|(
literal|"Last constant must be LUCENE_CURRENT"
argument_list|,
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
specifier|final
name|boolean
name|dep
init|=
name|Version
operator|.
name|class
operator|.
name|getField
argument_list|(
name|values
index|[
name|i
index|]
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|isAnnotationPresent
argument_list|(
name|Deprecated
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|+
literal|2
operator|!=
name|values
operator|.
name|length
condition|)
block|{
name|assertTrue
argument_list|(
name|values
index|[
name|i
index|]
operator|.
name|name
argument_list|()
operator|+
literal|" should be deprecated"
argument_list|,
name|dep
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
name|values
index|[
name|i
index|]
operator|.
name|name
argument_list|()
operator|+
literal|" should not be deprecated"
argument_list|,
name|dep
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testAgainstMainVersionConstant
specifier|public
name|void
name|testAgainstMainVersionConstant
parameter_list|()
block|{
specifier|final
name|Version
name|values
index|[]
init|=
name|Version
operator|.
name|values
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|values
operator|.
name|length
operator|>=
literal|2
argument_list|)
expr_stmt|;
specifier|final
name|String
name|mainVersionWithoutAlphaBeta
init|=
name|Constants
operator|.
name|mainVersionWithoutAlphaBeta
argument_list|()
decl_stmt|;
specifier|final
name|Version
name|mainVersionParsed
init|=
name|Version
operator|.
name|parseLeniently
argument_list|(
name|mainVersionWithoutAlphaBeta
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
literal|"Constant one before last must be the same as the parsed LUCENE_MAIN_VERSION (without alpha/beta) constant: "
operator|+
name|mainVersionWithoutAlphaBeta
argument_list|,
name|mainVersionParsed
argument_list|,
name|values
index|[
name|values
operator|.
name|length
operator|-
literal|2
index|]
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
