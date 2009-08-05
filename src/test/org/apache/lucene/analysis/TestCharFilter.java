begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package
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
DECL|class|TestCharFilter
specifier|public
class|class
name|TestCharFilter
extends|extends
name|LuceneTestCase
block|{
DECL|method|testCharFilter1
specifier|public
name|void
name|testCharFilter1
parameter_list|()
throws|throws
name|Exception
block|{
name|CharStream
name|cs
init|=
operator|new
name|CharFilter1
argument_list|(
name|CharReader
operator|.
name|get
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|""
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"corrected offset is invalid"
argument_list|,
literal|1
argument_list|,
name|cs
operator|.
name|correctOffset
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCharFilter2
specifier|public
name|void
name|testCharFilter2
parameter_list|()
throws|throws
name|Exception
block|{
name|CharStream
name|cs
init|=
operator|new
name|CharFilter2
argument_list|(
name|CharReader
operator|.
name|get
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|""
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"corrected offset is invalid"
argument_list|,
literal|2
argument_list|,
name|cs
operator|.
name|correctOffset
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCharFilter12
specifier|public
name|void
name|testCharFilter12
parameter_list|()
throws|throws
name|Exception
block|{
name|CharStream
name|cs
init|=
operator|new
name|CharFilter2
argument_list|(
operator|new
name|CharFilter1
argument_list|(
name|CharReader
operator|.
name|get
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|""
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"corrected offset is invalid"
argument_list|,
literal|3
argument_list|,
name|cs
operator|.
name|correctOffset
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCharFilter11
specifier|public
name|void
name|testCharFilter11
parameter_list|()
throws|throws
name|Exception
block|{
name|CharStream
name|cs
init|=
operator|new
name|CharFilter1
argument_list|(
operator|new
name|CharFilter1
argument_list|(
name|CharReader
operator|.
name|get
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|""
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"corrected offset is invalid"
argument_list|,
literal|2
argument_list|,
name|cs
operator|.
name|correctOffset
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|CharFilter1
specifier|static
class|class
name|CharFilter1
extends|extends
name|CharFilter
block|{
DECL|method|CharFilter1
specifier|protected
name|CharFilter1
parameter_list|(
name|CharStream
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
DECL|method|correct
specifier|protected
name|int
name|correct
parameter_list|(
name|int
name|currentOff
parameter_list|)
block|{
return|return
name|currentOff
operator|+
literal|1
return|;
block|}
block|}
DECL|class|CharFilter2
specifier|static
class|class
name|CharFilter2
extends|extends
name|CharFilter
block|{
DECL|method|CharFilter2
specifier|protected
name|CharFilter2
parameter_list|(
name|CharStream
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
DECL|method|correct
specifier|protected
name|int
name|correct
parameter_list|(
name|int
name|currentOff
parameter_list|)
block|{
return|return
name|currentOff
operator|+
literal|2
return|;
block|}
block|}
block|}
end_class
end_unit
