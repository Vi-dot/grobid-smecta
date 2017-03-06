import { DjinClientAdminPage } from './app.po';

describe('djin-client-admin App', function() {
  let page: DjinClientAdminPage;

  beforeEach(() => {
    page = new DjinClientAdminPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
