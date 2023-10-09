const Footer = () => {
  renderFooter();
};

function renderFooter() {

  const footer = document.querySelector("footer");
  footer.innerHTML =`
  <footer class="text-center text-lg-start fixed-bottom" id="fontColor">
        <div class="text-center p-3 text-light">
          <p>© 2023 Copyright:</p>
          <p class="text-light">Par Benbouchta Younes, Duong Quoc An, Nguyen Ngoc
            ,Queguineur, Yi Nghi Ke Man
          </p>
        </div>
      </footer>
  `


}

export default Footer;