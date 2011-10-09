begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Random
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
name|benchmark
operator|.
name|byTask
operator|.
name|utils
operator|.
name|Config
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
name|facet
operator|.
name|index
operator|.
name|CategoryContainer
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
name|facet
operator|.
name|taxonomy
operator|.
name|CategoryPath
import|;
end_import
begin_comment
comment|/**  * Simple implementation of a random facet source  *<p>  * Supports the following parameters:  *<ul>  *<li><b>rand.seed</b> - defines the seed to initialize Random with (default:<b>13</b>).  *<li><b>max.doc.facets</b> - maximal #facets per doc (default:<b>10</b>).  *    Actual number of facets in a certain doc would be anything between 1 and that number.  *<li><b>max.facet.depth</b> - maximal #components in a facet (default:<b>3</b>).  *    Actual number of components in a certain facet would be anything between 1 and that number.  *</ul>  */
end_comment
begin_class
DECL|class|RandomFacetSource
specifier|public
class|class
name|RandomFacetSource
extends|extends
name|FacetSource
block|{
DECL|field|random
name|Random
name|random
decl_stmt|;
DECL|field|maxDocFacets
specifier|private
name|int
name|maxDocFacets
init|=
literal|10
decl_stmt|;
DECL|field|maxFacetDepth
specifier|private
name|int
name|maxFacetDepth
init|=
literal|3
decl_stmt|;
DECL|field|maxValue
specifier|private
name|int
name|maxValue
init|=
name|maxDocFacets
operator|*
name|maxFacetDepth
decl_stmt|;
annotation|@
name|Override
DECL|method|getNextFacets
specifier|public
name|CategoryContainer
name|getNextFacets
parameter_list|(
name|CategoryContainer
name|facets
parameter_list|)
throws|throws
name|NoMoreDataException
throws|,
name|IOException
block|{
if|if
condition|(
name|facets
operator|==
literal|null
condition|)
block|{
name|facets
operator|=
operator|new
name|CategoryContainer
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|facets
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|int
name|numFacets
init|=
literal|1
operator|+
name|random
operator|.
name|nextInt
argument_list|(
name|maxDocFacets
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|// at least one facet to each doc
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numFacets
condition|;
name|i
operator|++
control|)
block|{
name|CategoryPath
name|cp
init|=
operator|new
name|CategoryPath
argument_list|()
decl_stmt|;
name|int
name|depth
init|=
literal|1
operator|+
name|random
operator|.
name|nextInt
argument_list|(
name|maxFacetDepth
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|// depth 0 is not useful
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|depth
condition|;
name|k
operator|++
control|)
block|{
name|cp
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
name|maxValue
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|addItem
argument_list|()
expr_stmt|;
block|}
name|facets
operator|.
name|addCategory
argument_list|(
name|cp
argument_list|)
expr_stmt|;
name|addBytes
argument_list|(
name|cp
operator|.
name|toString
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// very rough approximation
block|}
return|return
name|facets
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|// nothing to do here
block|}
annotation|@
name|Override
DECL|method|setConfig
specifier|public
name|void
name|setConfig
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|super
operator|.
name|setConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|random
operator|=
operator|new
name|Random
argument_list|(
name|config
operator|.
name|get
argument_list|(
literal|"rand.seed"
argument_list|,
literal|13
argument_list|)
argument_list|)
expr_stmt|;
name|maxDocFacets
operator|=
name|config
operator|.
name|get
argument_list|(
literal|"max.doc.facets"
argument_list|,
literal|200
argument_list|)
expr_stmt|;
name|maxFacetDepth
operator|=
name|config
operator|.
name|get
argument_list|(
literal|"max.facet.depth"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|maxValue
operator|=
name|maxDocFacets
operator|*
name|maxFacetDepth
expr_stmt|;
block|}
block|}
end_class
end_unit
