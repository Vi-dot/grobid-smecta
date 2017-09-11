import { SmectaPage } from './app.po';

describe('grobid-smecta App', function() {
  let page: SmectaPage;

  beforeEach(() => {
    page = new SmectaPage();
  });

  it('should display welcome message', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('Welcome to app!');
  });
});
