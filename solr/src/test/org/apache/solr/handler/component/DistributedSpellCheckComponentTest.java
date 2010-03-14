begin_unit
begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|BaseDistributedSearchTestCase
import|;
end_import
begin_comment
comment|/**  * Test for SpellCheckComponent's distributed querying  *  * @since solr 1.5  * @version $Id$  * @see org.apache.solr.handler.component.SpellCheckComponent  */
end_comment
begin_class
DECL|class|DistributedSpellCheckComponentTest
specifier|public
class|class
name|DistributedSpellCheckComponentTest
extends|extends
name|BaseDistributedSearchTestCase
block|{
annotation|@
name|Override
DECL|method|doTest
specifier|public
name|void
name|doTest
parameter_list|()
throws|throws
name|Exception
block|{
name|index
argument_list|(
name|id
argument_list|,
literal|"1"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"toyota"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"2"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"chevrolet"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"3"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"suzuki"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"4"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"ford"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"5"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"ferrari"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"6"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"jaguar"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"7"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"mclaren"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"8"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"sonata"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"9"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quick red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"10"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"blue"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"12"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"glue"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|handle
operator|.
name|clear
argument_list|()
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"QTime"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"timestamp"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"maxScore"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
comment|// we care only about the spellcheck results
name|handle
operator|.
name|put
argument_list|(
literal|"response"
argument_list|,
name|SKIP
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,lowerfilt"
argument_list|,
literal|"spellcheck.q"
argument_list|,
literal|"toyata"
argument_list|,
literal|"spellcheck"
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_BUILD
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"spellCheckCompRH"
argument_list|,
literal|"shards.qt"
argument_list|,
literal|"spellCheckCompRH"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,lowerfilt"
argument_list|,
literal|"spellcheck.q"
argument_list|,
literal|"toyata"
argument_list|,
literal|"spellcheck"
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_BUILD
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"spellCheckCompRH"
argument_list|,
literal|"shards.qt"
argument_list|,
literal|"spellCheckCompRH"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_EXTENDED_RESULTS
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,lowerfilt"
argument_list|,
literal|"spellcheck.q"
argument_list|,
literal|"bluo"
argument_list|,
literal|"spellcheck"
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_BUILD
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"spellCheckCompRH"
argument_list|,
literal|"shards.qt"
argument_list|,
literal|"spellCheckCompRH"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_EXTENDED_RESULTS
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COUNT
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"The quick reb fox jumped over the lazy brown dogs"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,lowerfilt"
argument_list|,
literal|"spellcheck"
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_BUILD
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"spellCheckCompRH"
argument_list|,
literal|"shards.qt"
argument_list|,
literal|"spellCheckCompRH"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_EXTENDED_RESULTS
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COUNT
argument_list|,
literal|"4"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COLLATE
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
